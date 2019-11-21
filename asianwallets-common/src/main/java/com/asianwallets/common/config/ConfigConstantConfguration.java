package com.asianwallets.common.config;
import com.asianwallets.common.constant.AsianWalletConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Wu, Hua-Zheng
 * @version v1.0.0
 * @classDesc: 功能描述: 项目配置文件初始化
 * @createTime 2018年8月2日 下午9:21:14
 * @copyright: 上海众哈网络技术有限公司
 */
@Slf4j
@Component
@Order(value = 1)
public class ConfigConstantConfguration implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {

		AsianWalletConstant.server_port = server_port;// 项目端口号
		AsianWalletConstant.project_name = project_name;// 项目名称



		log.info(">>>>>>>>>>>>>>>项目名称【{}】，端口号【{}】，项目配置加载完成 <<<<<<<<<<<<<", AsianWalletConstant.project_name,
				AsianWalletConstant.server_port);
	}

	@Value("${server.port}")
	private String server_port;// 项目端口号
	@Value("${spring.application.name}")
	private String project_name;// 项目名称



}
