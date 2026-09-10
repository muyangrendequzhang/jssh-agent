package com.myr.config;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.flow.agent.ParallelAgent;
import com.alibaba.cloud.ai.graph.agent.flow.agent.SequentialAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 服务器问题分析工作流：
 * 1. 剖析问题：1 个 agent 将问题拆解为分析要点
 * 2. 并行分析：3 个 agent 分别从技术、运维、安全三个角度并行分析
 * 3. 汇总报告：1 个 agent 综合并行分析结果，输出最终结论
 */
@Configuration
public class StateGraphConfiguration {

    @Bean(name = "integrateAgent")
    public SequentialAgent integrateAgent(@Qualifier("deepSeekChatModel") ChatModel chatModel,
                                          ToolCallbackProvider myTools) {
        // ============ 1. 剖析问题的 Agent ============
        ReactAgent problemAnalyzer = ReactAgent.builder()
                .name("problem_analyzer")
                .model(chatModel)
                .instruction("你是问题剖析专家。仔细阅读用户描述的问题，将其拆解为清晰的分析要点和排查方向，"
                        + "覆盖可能出现故障的各个层面。只输出剖析结果，不要执行工具。")
                .outputKey("problem_analysis")
                .saver(new MemorySaver())
                .build();

        // ============ 2. 三个并行分析角度的 Agent ============

        // 2.1 技术层面：进程、内存、网络等资源状况
        ReactAgent techAnalyzer = ReactAgent.builder()
                .name("tech_analyzer")
                .model(chatModel)
                .instruction("你是技术分析专家。基于问题剖析结果，调用工具查询进程、内存、CPU、网络等资源状况，"
                        + "从技术层面定位可能存在的问题。调用工具获取真实数据后再给出分析。")
                .toolCallbackProviders(myTools)
                .outputKey("analysis_tech")
                .saver(new MemorySaver())
                .build();

        // 2.2 运维层面：系统服务、文件等运行状态
        ReactAgent opsAnalyzer = ReactAgent.builder()
                .name("ops_analyzer")
                .model(chatModel)
                .instruction("你是运维分析专家。基于问题剖析结果，调用工具查询系统服务状态、文件目录结构等，"
                        + "从运维部署层面定位可能存在的问题。调用工具获取真实数据后再给出分析。")
                .toolCallbackProviders(myTools)
                .outputKey("analysis_ops")
                .saver(new MemorySaver())
                .build();

        // 2.3 安全层面：连接、账号、权限等安全隐患
        ReactAgent securityAnalyzer = ReactAgent.builder()
                .name("security_analyzer")
                .model(chatModel)
                .instruction("你是安全分析专家。基于问题剖析结果，调用工具查询已保存连接、进程所属用户、异常终端等，"
                        + "从安全层面排查可疑行为和隐患。调用工具获取真实数据后再给出分析。")
                .toolCallbackProviders(myTools)
                .outputKey("analysis_security")
                .saver(new MemorySaver())
                .build();

        // 并行执行三个分析 Agent，将结果合并到 analysis_results
        ParallelAgent parallelAnalysis = ParallelAgent.builder()
                .name("parallel_analysis")
                .description("从技术、运维、安全三个角度并行分析服务器问题")
                .subAgents(List.of(techAnalyzer, opsAnalyzer, securityAnalyzer))
                .mergeStrategy(new ParallelAgent.DefaultMergeStrategy())
                .mergeOutputKey("analysis_results")
                .build();

        // ============ 3. 汇总报告的 Agent ============
        ReactAgent summarizer = ReactAgent.builder()
                .name("summarizer")
                .model(chatModel)
                .instruction("你是汇总专家。综合 analysis_results 中的三个角度分析结果，"
                        + "提炼出一份完整的服务器问题分析报告，包含问题根因、影响范围和修复建议。")
                .saver(new MemorySaver())
                .build();

        // ============ 按顺序组合：剖析 → 并行分析 → 汇总 ============
        return SequentialAgent.builder()
                .name("server_analysis_workflow")
                .description("服务器问题分析工作流：剖析问题 → 多角度并行分析 → 汇总报告")
                .subAgents(List.of(problemAnalyzer, parallelAnalysis, summarizer))
                .build();
    }
}