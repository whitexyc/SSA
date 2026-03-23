package com.atguigu.study.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

/**
 * @auther zzyybs@126.com
 * @create 2025-07-26 16:25
 * @Description TODO
 */
@RestController
public class PromptTemplateController
{
    @Resource(name = "deepseek")
    private ChatModel deepseekChatModel;
    @Resource(name = "qwen")
    private ChatModel qwenChatModel;

    @Resource(name = "deepseekChatClient")
    private ChatClient deepseekChatClient;
    @Resource(name = "qwenChatClient")
    private ChatClient qwenChatClient;








    @Value("classpath:/prompttemplate/atguigu-template.txt")
    private org.springframework.core.io.Resource userTemplate;

    /**
     * @Description: PromptTemplate基本使用，使用占位符设置模版 PromptTemplate
     * @Auther: zzyybs@126.com
     * 测试地址
     * http://localhost:8006/prompttemplate/chat?topic=java&output_format=html&wordCount=200
     */
    @GetMapping("/prompttemplate/chat")
    public Flux<String> chat(String topic, String output_format, String wordCount)
    {
        PromptTemplate promptTemplate = new PromptTemplate("" +
                "讲一个关于{topic}的故事" +
                "并以{output_format}格式输出，" +
                "字数在{wordCount}左右");

        // PromptTempate -> Prompt
        Prompt prompt = promptTemplate.create(Map.of(
                "topic", topic,
                "output_format",output_format,
                "wordCount",wordCount));

        return deepseekChatClient.prompt(prompt).stream().content();
    }





    /**
     * @Description: PromptTemplate读取模版文件实现模版功能
     * @Auther: zzyybs@126.com
     * 测试地址
     * http://localhost:8006/prompttemplate/chat2?topic=java&output_format=html
     */
    @GetMapping("/prompttemplate/chat2")
    public String chat2(String topic,String output_format)
    {
        PromptTemplate promptTemplate = new PromptTemplate(userTemplate);

        Prompt prompt = promptTemplate.create(Map.of("topic", topic, "output_format", output_format));

        return deepseekChatClient.prompt(prompt).call().content();
    }


    /**
     *  @Auther: zzyybs@126.com
     * @Description:
     * 系统消息(SystemMessage)：设定AI的行为规则和功能边界(xxx助手/什么格式返回/字数控制多少)。
     * 用户消息(UserMessage)：用户的提问/主题
     * http://localhost:8006/prompttemplate/chat3?sysTopic=法律&userTopic=知识产权法
     *
     * http://localhost:8006/prompttemplate/chat3?sysTopic=法律&userTopic=夫妻肺片
     */
    @GetMapping("/prompttemplate/chat3")
    public Flux<String> chat3(String sysTopic, String userTopic)
    {
        // 1.SystemPromptTemplate
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate("你是{systemTopic}助手，只回答{systemTopic}其它无可奉告，以HTML格式的结果。");
        Message sysMessage = systemPromptTemplate.createMessage(Map.of("systemTopic", sysTopic));
        // 2.PromptTemplate
        PromptTemplate userPromptTemplate = new PromptTemplate("解释一下{userTopic}");
        Message userMessage = userPromptTemplate.createMessage(Map.of("userTopic", userTopic));
        // 3.组合【关键】 多个 Message -> Prompt
        Prompt prompt = new Prompt(List.of(sysMessage, userMessage));
        // 4.调用 LLM
        return deepseekChatClient.prompt(prompt).stream().content();
    }


    /**
     * @Description: 人物角色设定，通过SystemMessage来实现人物设定，本案例用ChatModel实现
     * 设定AI为”医疗专家”时，仅回答医学相关问题
     * 设定AI为编程助手”时，专注于技术问题解答
     * @Auther: zzyybs@126.com
     * http://localhost:8006/prompttemplate/chat4?question=牡丹花
     */
    @GetMapping("/prompttemplate/chat4")
    public String chat4(String question)
    {
        //1 系统消息
        SystemMessage systemMessage = new SystemMessage("你是一个Java编程助手，拒绝回答非技术问题。");
        //2 用户消息
        UserMessage userMessage = new UserMessage(question);
        //3 系统消息+用户消息=完整提示词
        //Prompt prompt = new Prompt(systemMessage, userMessage);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        //4 调用LLM
        String result = deepseekChatModel.call(prompt).getResult().getOutput().getText();
        System.out.println(result);
        return result;
    }

    /**
     * @Description: 人物角色设定，通过SystemMessage来实现人物设定，本案例用ChatClient实现
     * 设定AI为”医疗专家”时，仅回答医学相关问题
     * 设定AI为编程助手”时，专注于技术问题解答
     * @Auther: zzyybs@126.com
     * http://localhost:8006/prompttemplate/chat5?question=火锅
     */
    @GetMapping("/prompttemplate/chat5")
    public Flux<String> chat5(String question)
    {
        return deepseekChatClient.prompt()
                .system("你是一个擅长深度思考和清晰表达的专家思维模型。你擅长以人类的方式思考和回答问题,分层次,由浅入深, 循序渐进的思考问题 ,你主要为我解决算法和八股后端的问题,Java作为示例语言\n" +
                        "\n" +
                        "回答前先直接给出答案或示例,必须用和小白解释的语言\n" +
                        "\n" +
                        "你追求的是彻底的清晰。发散你的思维, 联想相关的概念,站在不同的视角和角度思考,要生动形象,给出你自己的思考和想法,不要重复解释我术语的定义,要联想到相关的其他概念\n" +
                        "\n" +
                        "你将模拟人类顶尖专家的思考模式：你的回答将是一场逐步深入的探索之旅，旨在构建坚实而深刻的理解。你需要像一位思想家对着白板梳理思路一样,从最最基础、最不言自明（但往往被忽略）的原理或定义开始。\n" +
                        "\n" +
                        "想象你在对一个对该领域几乎一无所知但充满好奇心的初学者——讲话。语速要慢，逻辑推进要平缓。要让用户明白,为什么需要这么做,核心是什么,你需要清晰揭示其核心本质, 通过通俗的语言、层层深入地阐释其底层逻辑和本质原理。确保你的解释结构能够引导新自然地思考和理\n" +
                        "\n" +
                        "\n" +
                        "强调:输出html页面,输出结构美观\n" +
                        "\n" +
                        "我最近正在准备面试,希望最后展示一下在面试中该怎么回答,要思考面试官如果这么问希望听到的回答是怎样的? 哪些是重点,最后给一个面试回答,整体是一个回答\n" +
                        "\n" +
                        "以上所有的内容为我的要求或问题,要让我直观的理解流程和实现,接下来请回答:\n" +
                        "")
                .user(question)
                .stream()
                .content();
    }
}
