package com.hdtx.base.common.spring.refresh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Set;

public class DbRefreshCommand {

    private static final Logger logger = LoggerFactory.getLogger(DbRefreshCommand.class);


    public static final DbInfo EMPTY_DB_INFO = new DbInfo(null, 0, null);

    private boolean refreshAll = false;

    private Set<DbInfo> dbInfos;

    public boolean isRefreshAll() {
        return refreshAll;
    }

    public void setRefreshAll(boolean refreshAll) {
        this.refreshAll = refreshAll;
    }

    public Set<DbInfo> getDbInfos() {
        return dbInfos;
    }

    public void setDbInfos(Set<DbInfo> dbInfos) {
        this.dbInfos = dbInfos;
    }

    public static class DbInfo {

        private String host;

        private int port;

        private DbType dbType;

        public DbInfo() {
        }

        public DbInfo(String host, int port, DbType dbType) {
            this.host = host;
            this.port = port;
            this.dbType = dbType;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public DbType getDbType() {
            return dbType;
        }

        public void setDbType(DbType dbType) {
            this.dbType = dbType;
        }

        public boolean isNull() {
            return host == null || port <= 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DbInfo dbInfo = (DbInfo) o;

            if (port != dbInfo.port) return false;
            return host != null ? host.equals(dbInfo.host) : dbInfo.host == null;
        }

        @Override
        public int hashCode() {
            int result = host != null ? host.hashCode() : 0;
            result = 31 * result + port;
            return result;
        }

        public static DbInfo fromJdbcUrl(String jdbcUrl) {
            String cleanURI = null;
            DbType dbType = DbType.UNKNOWN;
            String host = null;
            int port = 0;

            try {
                if(jdbcUrl.startsWith("jdbc:jtds")) {
                    cleanURI = jdbcUrl.substring(10);
                    dbType = DbType.SQLSERVER;
                } else if(jdbcUrl.startsWith("jdbc:mysql")) {
                    cleanURI = jdbcUrl.substring(5);
                    dbType = DbType.MYSQL;
                }
                if(cleanURI != null) {
                    URI uri = URI.create(cleanURI);
                    host = uri.getHost();
                    port = uri.getPort();
                    if(port == -1) {
                        //没有填端口, 使用默认端口
                        switch (dbType) {
                            case SQLSERVER:
                                port = 1433;
                                break;
                            case MYSQL:
                                port = 3306;
                                break;
                        }
                    }
                }
                return new DbInfo(host, port, dbType);

            } catch (Exception e) {
                logger.error("从jdbcUrl中解析host和port失败, jdbcUrl: " + jdbcUrl, e);
                return EMPTY_DB_INFO;
            }
        }
    }

    public enum DbType {
        UNKNOWN, MYSQL, SQLSERVER
    }

}
