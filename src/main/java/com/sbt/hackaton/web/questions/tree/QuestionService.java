package com.sbt.hackaton.web.questions.tree;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbt.hackaton.web.questions.tree.dto.AnswerDto;
import com.sbt.hackaton.web.questions.tree.dto.QuestionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class QuestionService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final UUID reviewUUID = UUID.randomUUID();

    @Autowired
    private ObjectMapper objectMapper;

    private QuestionDto rootDto;
    private Map<UUID, QuestionDto> tree;
    private Map<UUID, AnswerDto> answers = new HashMap<>();

    @PostConstruct
    public void init() throws Exception {

        URL url = getClass().getResource("/questions.json");

        final Map<UUID, QuestionDto> nodes = new HashMap<>();
        QuestionNode root = objectMapper.readValue(url, QuestionNode.class);

        logger.error("root={}", root);

        rootDto = createQuestionDtoByNode(root);
        fillNodes(nodes, root);

        tree = nodes;

        logger.info("rootDto: {}", rootDto);
        logger.info("tree: {}", tree);

        answers.put(reviewUUID, new AnswerDto(reviewUUID, "Предложение клиенту оставить отзыв", true));
    }

    private void fillNodes(final Map<UUID, QuestionDto> nodes, final QuestionNode parent) {
        parent.getAnswers().forEach(answerNode -> {
            if (!answerNode.isTerminate()) {
                final QuestionNode childQuestionNode = answerNode.getQuestionNode();
                nodes.put(answerNode.getId(), createQuestionDtoByNode(childQuestionNode));
                fillNodes(nodes, childQuestionNode);
            }
        });
    }

    private void addReviewAnswer(QuestionDto questionDto){
        questionDto.getAnswers().add(new AnswerDto(reviewUUID, "Оставить свой отзыв", true));
    }

    public QuestionDto getRoot() {
        return rootDto;
    }

    public QuestionDto getNext(UUID answerId) {
        QuestionDto questionNode = tree.get(answerId);
        if (questionNode == null) {
            throw new IllegalArgumentException("Отсутствует вопрос для ответа с id = " + answerId);
        }
        return questionNode;
    }

    public AnswerDto getAnswer(UUID answerId) {
        return answers.get(answerId);
    }

    private QuestionDto createQuestionDtoByNode(final QuestionNode node) {
        logger.error("!!!!node: {}", node);
        node.setId(UUID.randomUUID()); // todo удалить генерацию id
        QuestionDto questionDto = new QuestionDto();
        questionDto.setId(node.getId());
        questionDto.setQuestionText(node.getQuestionText());

        questionDto.setAnswers(node.getAnswers().stream()
                .peek(answerNode -> {
                    answerNode.setId(UUID.randomUUID()); // todo удалить генерацию id
                })
                .map(answerNode ->
                        new AnswerDto(answerNode.getId(), answerNode.getAnswer(), answerNode.isTerminate())
                ).peek(answerDto -> answers.put(answerDto.getId(), answerDto))
                .collect(Collectors.toList()));

         addReviewAnswer(questionDto);
         return questionDto;
    }

    public boolean isReviewAnswer(UUID id) {
        return reviewUUID.equals(id);
    }
}
