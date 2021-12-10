# 工程简介
使用Aop动态代理，应用mybtis拦截器实现数据表的分表操作

# 模块介绍
shard：分表组件核心逻辑实现子模块（可打包提交到私服共用）

# 应用引入使用
1）在配置文件添加分表规则，tableName指定表名，tableNumber指定分表数量
com:
    xiaochen:
        starter:
            shard:
                enabled: false
                mybatis:
                    table:
                        rules:
                            - tableName: user
                              tableNumber: 2
                            - tableName: user_info
                              tableNumber: 2

2）在分表服务接口上添加分表拦截功能注解，如下：
@ShardTableAnnotation(tableName = "T_XXX", shardKey = "#userId")
public User findUserById(String userId) {
    return userMapper.queryUserById(userId);
}
3）项目所用sql脚本在test子模块resource目录下

# 导入运行
1）github下载项目到本地，导入到IDE
2）cd到项目主路径，执行编译命令：mvn clean install -U
注意：如编译报错，test子模块找不到shard.jar依赖包，修改本模块的pom文件jar路径为本机绝对路径即可
3）测试入口:TestApplication

# 其他
