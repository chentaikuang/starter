# 工程简介
使用钉钉群机器人工具，开发钉钉群消息发送通知starter

# 模块介绍
dingtalk：钉钉消息通知组件核心逻辑实现子模块（可打包提交到私服共用）

# 应用引入使用
1）在配置文件添加钉钉群机器人token信息，替换对于的token、secret
com:
    xiaochen:
        starter:
            dingtalk:
                enabled: true
                apps:
                    - name: test
                      token: xxx
                      secret: yyy

2）starter内部逻辑根据配置中的name选择对应的群token（找不到则默认选择配置最前面的token）发送消息

# 导入运行
1）github下载项目到本地，导入到IDE
2）cd到项目主路径，执行编译命令：mvn clean install -U
注意：如编译报错，test子模块找不到dingtalk.jar依赖包，修改本模块的pom文件jar路径为本机绝对路径即可
3）测试入口:TestApplication

# 其他
