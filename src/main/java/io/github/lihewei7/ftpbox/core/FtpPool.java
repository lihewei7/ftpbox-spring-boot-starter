package io.github.lihewei7.ftpbox.core;

import io.github.lihewei7.ftpbox.config.PoolProperties;
import io.github.lihewei7.ftpbox.config.FtpProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.*;

import java.util.LinkedHashMap;

/**
 * @author: lihewei
 */
public class FtpPool {
    private static final Log _logger = LogFactory.getLog(FtpPool.class);
    public static final String COULD_NOT_GET_A_RESOURCE_FROM_THE_POOL = "Could not get a resource from the pool";
    private GenericObjectPool<FtpClient> genericFtpPool;
    private GenericKeyedObjectPool<String, FtpClient> genericKeyedFtpPool;

    public FtpPool(FtpProperties ftpProperties, PoolProperties poolProperties) {
        this.genericFtpPool = new GenericObjectPool<>(new PooledClientFactory(ftpProperties), getPoolConfig(poolProperties));
        _logger.info("FTPBox: Created");
    }

    public FtpPool(LinkedHashMap ftpPropertiesMap, PoolProperties poolProperties){
        this.genericKeyedFtpPool = new GenericKeyedObjectPool<>(new keyedPooledClientFactory(ftpPropertiesMap),getKeyedPoolConfig(poolProperties));
        _logger.info("multiple-host FTPBox Successfully created");
    }

    /**
     * Check whether it is a single host.
     */
    public boolean isUniqueHost() {
        return genericFtpPool != null;
    }

    /**
     * Obtain an ftp connection from the pool.
     */
    public FtpClient borrowObject(String key) {
        try {
            return key == null ?
                    genericFtpPool.borrowObject() : genericKeyedFtpPool.borrowObject(key);
        } catch (Exception e) {
            throw new PoolException(COULD_NOT_GET_A_RESOURCE_FROM_THE_POOL, e);
        }
    }

    /**
     * The ftp connection is returned to the pool.
     */
    public void returnObject(String key, FtpClient ftpClient) {
        try {
            if (key == null){
                genericFtpPool.returnObject(ftpClient);
            }else {
                genericKeyedFtpPool.returnObject(key, ftpClient);
            }
        } catch (Exception e) {
            throw new PoolException("Could not return a resource from the pool", e);
        }
    }

    /**
     * The ftp connection is destroyed from the pool.
     */
    public void invalidateObject(String key, FtpClient ftpClient) {
        try {
            if (key == null){
                genericFtpPool.invalidateObject(ftpClient);
            }else {
                genericKeyedFtpPool.invalidateObject(key, ftpClient);
            }
        } catch (Exception e) {
            throw new PoolException("Could not invalidate the broken resource", e);
        }
    }


    private static class PooledClientFactory extends BasePooledObjectFactory<FtpClient> {

        private final FtpProperties ftpProperties;

        public PooledClientFactory(FtpProperties ftpProperties) {
            this.ftpProperties = ftpProperties;
        }

        @Override
        public FtpClient create() {
            return new FtpClient(ftpProperties);
        }

        @Override
        public PooledObject<FtpClient> wrap(FtpClient ftpClient) {
            return new DefaultPooledObject<>(ftpClient);
        }

        @Override
        public boolean validateObject(PooledObject<FtpClient> p) {
            return p.getObject().test();
        }

        @Override
        public void destroyObject(PooledObject<FtpClient> p) {
            p.getObject().disconnect();
        }

    }


    private static class keyedPooledClientFactory extends BaseKeyedPooledObjectFactory<String, FtpClient> {

        private LinkedHashMap<String, FtpProperties> ftpPropertiesMap;

        public keyedPooledClientFactory(LinkedHashMap ftpPropertiesMap){
            this.ftpPropertiesMap = ftpPropertiesMap;
        }

        @Override
        public FtpClient create(String key) {
            return new FtpClient(ftpPropertiesMap.get(key));
        }

        @Override
        public PooledObject<FtpClient> wrap(FtpClient ftpClient) {
            return new DefaultPooledObject<>(ftpClient);
        }

        @Override
        public void destroyObject(String key, PooledObject<FtpClient> p) {
            p.getObject().disconnect();
        }

        @Override
        public boolean validateObject(String key, PooledObject<FtpClient> p) {
            return p.getObject().test();
        }
    }

    private GenericObjectPoolConfig<FtpClient> getPoolConfig(PoolProperties poolProperties) {
        GenericObjectPoolConfig<FtpClient> config = commonPoolConfig(new GenericObjectPoolConfig<>(), poolProperties);
        config.setMinIdle(poolProperties.getMinIdle());
        config.setMaxIdle(poolProperties.getMaxIdle());
        config.setMaxTotal(poolProperties.getMaxActive());
        return config;
    }

    private GenericKeyedObjectPoolConfig<FtpClient> getKeyedPoolConfig(PoolProperties poolProperties) {
        GenericKeyedObjectPoolConfig<FtpClient> config = commonPoolConfig(new GenericKeyedObjectPoolConfig<>(), poolProperties);
        config.setMinIdlePerKey(poolProperties.getMinIdle());
        config.setMaxIdlePerKey(poolProperties.getMaxIdle());
        config.setMaxTotalPerKey(poolProperties.getMaxActivePerKey());
        config.setMaxTotal(poolProperties.getMaxActive());
        return config;
    }

    private <T extends BaseObjectPoolConfig<FtpClient>> T commonPoolConfig(T config, PoolProperties poolProperties) {
        config.setMaxWaitMillis(poolProperties.getMaxWait());
        config.setTestOnBorrow(poolProperties.isTestOnBorrow());
        config.setTestOnReturn(poolProperties.isTestOnReturn());
        config.setTestWhileIdle(poolProperties.isTestWhileIdle());
        config.setTimeBetweenEvictionRunsMillis(poolProperties.getTimeBetweenEvictionRuns());
        config.setMinEvictableIdleTimeMillis(poolProperties.getMinEvictableIdleTimeMillis());
        return config;
    }
}
