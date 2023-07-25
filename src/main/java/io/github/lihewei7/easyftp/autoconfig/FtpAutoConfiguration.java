package io.github.lihewei7.easyftp.autoconfig;

import io.github.lihewei7.easyftp.config.PoolProperties;
import io.github.lihewei7.easyftp.config.FtpProperties;
import io.github.lihewei7.easyftp.core.HostsManage;
import io.github.lihewei7.easyftp.core.FtpPool;
import io.github.lihewei7.easyftp.core.FtpTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * @author: lihewei
*/
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({FtpProperties.class, PoolProperties.class})
public class FtpAutoConfiguration {

    @Bean
    public FtpPool ftpPool(FtpProperties ftpProperties, PoolProperties poolProperties) {
        return ftpProperties.getHosts() == null ?
                new FtpPool(ftpProperties, poolProperties) :
                new FtpPool(HostsManage.initHostKeys(ftpProperties.getHosts()),poolProperties);
    }

    @Bean
    @DependsOn("ftpPool")
    public FtpTemplate sftpTemplate(FtpPool ftpPool) {
        return new FtpTemplate(ftpPool);
    }
}
