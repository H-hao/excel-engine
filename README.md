# Excel 导入导出
## 功能
### 可配置项
#### EngineConfiguration 配置
1. 通用
    - globalRollbackThreshold：全局失败回滚的阈值，如果失败条数大于指定的数字，则回滚所有数据，否则提交成功的数据；也可以在 excelImport 中进行配置
1. Spring
#### xml 配置
1. `excelExport`
    - 属性：`id`，`fileName`，`exportType`，`freezeTop`，`styleRef`，`headerStyleRef`，`maxColumnWidth`，`maxRowHeight`...
        - styleRef：
        - headerStyleRef：引用的表头样式，默认为：加粗，12号字体，微软雅黑
    - 子标签：Map（Entry（属性：`header`，`getter`，`dataFormat`，`width`，`datasource`，`selectSql`；子标签：`getter`））
        - width：设置当前列的宽度，如果设置了宽度，则 autoWidth 为 false，如果没有设置 width，那么 autoWidth，即自动调列宽
        - dataFormat：格式化单元格，默认支持：General(常规)，`int`，`double`，`Date`，`DateTime`，`ZhDate`，`ZhDateTime`，`String`，注意：如果包含中文，导入时则可能无法正确的转换为指定的格式
1. `excelImport`
    - 属性
        - `id`：在 xml 中的唯一 id；
        - `type`：将每一行的数据封装成此 type 的实例；
        - `isIndexWay`: 是否是通过索引的方式来进行列与 getter/setter 的映射的，它的值会在设置了 startIndexForTemplate 或 templateLocation 之后被设置为 true，默认为 false，即默认是通过 header 的方式进行映射的；
        - `startIndexForTemplate`: 
        - `isConsecutive`: 
        - `ignoreColumnIndex`: 
    - 子标签
        - Entry（Map 的子标签）
            - 属性
                - `header`：表头名称
                - `setter`：setter 表达式，如：objA.propertyB
                - `copyTo`：将此单元格的值，顺带复制到指定的属性上，以“,”分隔；
                - `blank`：如果当前单元格为空，则添加指定的值到对象属性上；
                - `dataSource`：数据源，如果需要切换数据源，需要实现`DataSource`接口；
                - `selectSql`：查询 sql，有且只有一个参数（当前 cell 的值），如：```select name form table where id=?```；
            - 子标签
                - `setter`：可以进行一些 if 的判断，来选择最终将值存储在哪一个属性上，从上到下判断，到第一个true或所有判断执行完毕时结束赋值；if 内的 test 属性值如果重复，则只有最后一个为有效的；
1. `excelMap`
1. `style`
    - 属性
        - `id`
        - `extend`
    - 子标签
        - property
            - `font`
            - `wrapText`
            - `alignment`
            - `verticalAlignment`
1. `font`
    - 默认的 font：默认会有一个font（微软雅黑，12号字体），自定义字体可以选择继承此 font
    - 属性
        - `id`
        - `extend`
    - 子标签
        - property
            - `fontName`
            - `bold`
            - `fontHeightInPoints`
## 进度
1. xml
    - app.xml 
        - globalMaxColumnWidth：赋值到 excelOfExportVo 对象的 maxColumnWidth 属性上，目前没有使用 global，直接使用的 excelOfExportVo 对象上的 maxColumnWidth 的默认值
        - globalMaxRowHeight，
        - 回滚阈值：是否需要，目前只是返回一个 list 对象，并没有执行保存的 sql，所以不需要此属性
        - 将回滚阈值修改为通用阈值（FailThreshold）， 例如：导入导出成功的阈值， 即：当有多少行，没行有多少列存在错误时，就抛出异常（自定义异常： OutOfFailThresholdException）到最外层；
            - 要不要把行列的失败次数分开？globalRowFailThreshold, globalColumnFailThreshold
            - 为 xml 配置中导入导出节点添加相应的阈值配置
            - 默认的情况下的策略：全部正确（√）？还是不管是否有错误？
            - catch 的时候，catch 哪些异常？是指定的异常还是大的 Exception？未完成
            - 未完成（row，cell完成(导出导入)，sheet还没动，导入导出都未完成），测试完成
    - excel.xml
        - 页眉页脚：页面：headerOfPage, 页脚：footerOfPage；
        - 正确的应该是：现有的 导出导入节点应该对应 excel 的 sheet，可以再添加一个节点（workbook）用来表示workbook，其中的属性 sheets，可以引用其他的 导出导入节点；
1. parse
    - cell 之间的依赖关系的检查：是否需要？未完成
    - else, other 的解析，未完成
    - excelMap 配置的解析（可引用的 映射 配置），未完成
        - 是否需要？一般来说，导入导出所涉及到的实例类是相同的，属性也是相同的；但是，如果需要 selectSql，那么仍然需要单独为他们配置这些特有的配置项；
    - global 属性的迁移（需要重新设计，因为全局属性在 engineConfiguration 中，而对象的实例化是在 parser 中进行的，所以属性的迁移需要在 parser 中完成），未完成
1. builder
    - 为 configuration 添加 builder 类，用于没有 spring 环境时，配置的快速完成；
1. engine
    - 普通导入：80%
        - 数据源的切换，以及注入自定义的接口实现，完成!
            - 但是，是否需要提供一个默认的实现？，提供了一个基于 spring 的实现？（未完成）
        - 导入的最终结果只是一个 List，由用户进行处理;
        - 对导入的数据进行检查，添加一些必须包含的列（entry 中 require=true），只有导入才需要 require 配置，完成
        - 通过模版导入：完成
            - 其实只是使用索引替换 header，并不会使用 templateLocation，使用的是 startIndexForTemplate， isConsecutive， ignoreColumnIndex；
    - 普通导出：80%
        - 宽度的解析与设置，完成
        - 全局的宽度，高度：未完成
        - 通过模版导出：完成（没有提供文件服务器的实现）
            - xml：配置 templateLocation，配置 cell 的索引(cellIndex) 与 getter 的关系
                - location 的配置：如果就是当前项目下，则直接使用 templateLocation 配置，如果文件在文件服务器上，那怎么处理（提供一个接口，注入用户的实现，然后调用方法获取文件，再进行读取？）进度：提供了接口和默认的file实现，未提供文件服务器的实现
                - 索引：两种方式
                    - 方式一：配置一个起始索引(startIndexForTemplate)，配置一个是否直接连续自增的索引标记(isConsecutive)，再配置getter序列（需要顺序相同）；进度：完成
                    - 方式二：配置每一个索引与getter的映射关系；进度：完成
                    - 两种方式同时存在时，优先方式二；
                - 忽略的列：添加属性 ignoreColumnIndex ，用以描述那些不需要值的列，在设置值到 cell 中时，这些列将直接跳过；格式：以逗号分开的字母，进度：完成
            - 解析：在 excelEntry 中添加一个字段，cellIndex 表示要将当前获取到的值写入指定所有的cell中，解析时，将 cellIndex 作为 excelOfExportVo 中的 excelMap 的 key；进度：完成
            - 导出：普通需要 header 的，现在需要 cellIndex；进度：完成
    - web导入：0%，不需要？直接转换为流后使用普通导入？
    - web导出：80%，与普通导入进度相同，因为 请求响应的处理都已处理；
    
## 日志记录
>2017-07-10
>> - 添加：模版导出方式
>
>2017-07-11
>> - 添加： required 的支持
>> - 添加： 导入使用模版（索引）的方式；
>> - 添加：失败策略，完成了 cell，row 的失败抛出策略（requiredException不计入此策略的次数中），还未测试；
>
>2017-07-12
>> - 测试 失败策略，完成；
>> - 全局属性的迁移（需要重新设计，因为全局属性在 engineConfiguration 中，而对象的实例化是在 parser 中进行的，所以属性的迁移需要在 parser 中完成；）
>   
>2017-07-30
>> - 添加测试文件，后续再补测试内容
>> - 为 configuration 添加 builder 类，用于没有 spring 环境时，配置的快速完成；
>