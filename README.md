# 数据采集服务
# 项目的部署
# 启动脚本要添加机器编号参数（machineNo）
# 参数值为1、2、3...不能重复

# 对接使用
# 定义采集参数接口，使用线程池去写文件
# 每天定时把前一天的数据文件拷贝到远程cos
# 删除昨天的文件目录

# 项目结构说明
# collect-api:接口定义、文件上传参数配置
# collect-web:接口定义入口
# collect-common:枚举、公共工具类
# collect-datasource:以后操作数据库使用
# collect-service:接口实现层