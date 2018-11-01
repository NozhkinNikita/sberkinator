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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class QuestionService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ObjectMapper objectMapper;

    private QuestionDto rootDto;
    private Map<Pair, QuestionDto> tree;

    @PostConstruct
    public void init() throws Exception {

        URL url = getClass().getResource("/questions.json");

        final Map<Pair, QuestionDto> nodes = new HashMap<>();
        QuestionNode root = objectMapper.readValue(url, QuestionNode.class);

        logger.error("root={}", root);

        fillNodes(nodes, root);

        rootDto = createQuestionDtoByNode(root);
        tree = nodes;

        logger.info("rootDto: {}", rootDto);
        logger.info("tree: {}", tree);
    }

    private void fillNodes(final Map<Pair, QuestionDto> nodes, final QuestionNode parent) {
        parent.setId(UUID.randomUUID());
        parent.getAnswers().forEach(answerNode -> {
            if (!answerNode.isTerminate()) {
                Pair pair = new Pair(parent.getId(), answerNode.getKey());
                final QuestionNode childQuestionNode = answerNode.getQuestionNode();
                fillNodes(nodes, childQuestionNode);
                nodes.put(pair, createQuestionDtoByNode(childQuestionNode));
            }
        });
    }

    public QuestionDto getRoot() {
        return rootDto;
    }

    public QuestionDto getNext(UUID questionId, String answerKey) {
        Pair key = new Pair(questionId, answerKey);
        QuestionDto questionNode = tree.get(key);
        if (questionNode == null) {
            throw new IllegalArgumentException("Отсутствует вопрос по ключу " + key);
        }
        return questionNode;
    }

    private QuestionDto createQuestionDtoByNode(final QuestionNode node) {
        QuestionDto questionDto = new QuestionDto();
        questionDto.setId(node.getId());
        questionDto.setQuestionText(node.getQuestionText());

        questionDto.setAnswers(node.getAnswers().stream().map(answerNode ->
                new AnswerDto(answerNode.getKey(), answerNode.getAnswer(), answerNode.isTerminate())
        ).collect(Collectors.toList()));

        return questionDto;
    }
}
