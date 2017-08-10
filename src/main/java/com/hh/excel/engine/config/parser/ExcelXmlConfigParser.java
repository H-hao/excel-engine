package com.hh.excel.engine.config.parser;

import com.hh.excel.engine.common.CommonUtil;
import com.hh.excel.engine.common.Constants;
import com.hh.excel.engine.common.CoreUtil;
import com.hh.excel.engine.config.vo.*;
import ognl.Node;
import ognl.Ognl;
import ognl.OgnlException;
import ognl.SimpleNode;
import org.apache.commons.beanutils.PropertyUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@SuppressWarnings("rawtypes")
public class ExcelXmlConfigParser extends ExcelConfigParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelXmlConfigParser.class);
    private static final String SUFFIX = ".xml";
    public static final String ELEMENT_SHEET = "sheet";
    private Element root = null;

    // 取消默认xml配置，没有手动配置就不读取任何文件
    public ExcelXmlConfigParser() {
        // this(Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_MAPPER_FILE_NAME + SUFFIX));
    }

    /*public ExcelXmlConfigParser(InputStream inputStream) {
        try {
            SAXReader saxReader = new SAXReader();
            // 忽略 dtd
            saxReader.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.LOAD_EXTERNAL_DTD_FEATURE, false);
            Document document = saxReader.read(inputStream);
            root = document.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("读取xml文件失败");
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    protected void doConfig() {
        // 读取多个 inputStream
        for (InputStream inputStream : inputStreams) {
            try {
                SAXReader saxReader = new SAXReader();
                saxReader.setFeature(com.sun.org.apache.xerces.internal.impl.Constants.XERCES_FEATURE_PREFIX + com.sun.org.apache.xerces.internal.impl.Constants.LOAD_EXTERNAL_DTD_FEATURE, false);// 忽略 dtd
                Document document = saxReader.read(inputStream);// 这里会自己关闭流
                root = document.getRootElement();
                // 读取xml配置 List elements = root.elements();
                readXml2Configuration();
                root = null;
            } catch (DocumentException | SAXException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("读取xml文件失败");
            }
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("读取 xml 配置(size=[{}])完成", inputStreams.size());
        }
    }

    private void readXml2Configuration() {
        // 为了解决依赖关系，可以手动处理优先级，优先读取被依赖的配置
        // 如：加载顺序 font->cellStyle->excelMap->excelExport或excelImport
        readFont();
        readCellStyle();
        // TODO 读取 页眉页脚
        // readHeaderAndFooter();
        readExcelMap();
        readExcelImport();
        readExcelExport();
        // readAll();
    }

    /**
     * @author huanghao
     * @date 2017年4月7日下午3:03:00
     */
    private void readFont() {
        List fontElements = root.elements(Constants.ELEMENT_FONT);
        for (Object obj : fontElements) {
            Element element = (Element) obj;
            AbstractBaseConfig baseConfig = new FontVo();
            setEasyProperty(element, baseConfig);
            configuration.getBaseConfigs().put(element.attributeValue("id"), baseConfig);
        }
    }

    /**
     * @author huanghao
     * @date 2017年4月7日下午3:03:12
     */
    private void readCellStyle() {
        List cellStyleElements = root.elements(Constants.ELEMENT_STYLE);
        for (Object obj : cellStyleElements) {
            Element element = (Element) obj;
            AbstractBaseConfig baseConfig = new CellStyleVo();
            setEasyProperty(element, baseConfig);
            configuration.getBaseConfigs().put(element.attributeValue("id"), baseConfig);
        }
    }

    /**
     * @author huanghao
     * @date 2017年4月7日下午3:03:22
     */
    private void readExcelMap() {
        List excelMapElements = root.elements(Constants.ELEMENT_EXCEL_MAP);
        for (Object obj : excelMapElements) {
            Element element = (Element) obj;
            AbstractBaseConfig baseConfig = new ExcelMap();
            setMappingProperty(element, baseConfig);
            configuration.getBaseConfigs().put(element.attributeValue("id"), baseConfig);
        }
    }

    /**
     * @author huanghao
     * @date 2017年4月7日下午3:03:32
     */
    private void readExcelImport() {
        List excelImportElements = root.elements(Constants.ELEMENT_EXCEL_IMPORT);
        for (Object obj : excelImportElements) {
            Element element = (Element) obj;// sheet
            ExcelVo baseConfig = new ExcelOfImportVo();
            // read attr of excelImport node
            readAttr2Obj(element, baseConfig);
            // 读取 sheet 节点
            List sheetElements = element.elements(Constants.ELEMENT_SHEET_IMPORT);
            List<SheetVo> sheetVos = baseConfig.getSheetVos();
            for (Object sheetElement : sheetElements) {
                Element sheet = (Element) sheetElement;
                SheetForImportVo sheetConfig = new SheetForImportVo();
                sheetConfig.setExcelVo(baseConfig);
                // 读取子节点
                setComplexProperty(sheet, sheetConfig);
                sheetVos.add(sheetConfig);
            }
            // 处理 导入excel 时的依赖关系
            parseDependencies((ExcelOfImportVo) baseConfig);
            // 检查依赖关系
            checkDependencies((ExcelOfImportVo) baseConfig);
            // 检查他们之间的依赖关系
            configuration.getBaseConfigs().put(element.attributeValue("id"), baseConfig);
        }
    }

    /**
     * @author huanghao
     * @date 2017年4月7日下午3:03:46
     */
    private void readExcelExport() {
        List excelExportElements = root.elements(Constants.ELEMENT_EXCEL_EXPORT);
        for (Object obj : excelExportElements) {
            Element element = (Element) obj;// sheet
            ExcelVo baseConfig = new ExcelOfExportVo();
            // read excelExport node attr
            readAttr2Obj(element, baseConfig);
            // read sheet node
            List sheetElements = element.elements(Constants.ELEMENT_SHEET_EXPORT);
            List<SheetVo> sheetVos = baseConfig.getSheetVos();
            for (Object sheetElement : sheetElements) {
                Element sheet = (Element) sheetElement;
                SheetForExportVo sheetConfig = new SheetForExportVo();
                sheetConfig.setExcelVo(baseConfig);
                setComplexProperty(sheet, sheetConfig);
                sheetVos.add(sheetConfig);
            }
            // 处理 导入excel 时的依赖关系
            // parseDependencies((ExcelOfImportVo) baseConfig);
            // 检查依赖关系
            // checkDependencies((ExcelOfImportVo) baseConfig);
            configuration.getBaseConfigs().put(element.attributeValue("id"), baseConfig);
        }
    }

    /**
     * 读取xml所有内容
     *
     * @author huanghao
     * @date 2017年4月7日下午3:04:13
     */
    @Deprecated
    private void readAll() {
        for (Iterator elementIterator = root.elementIterator(); elementIterator.hasNext(); ) {
            Element element = (Element) elementIterator.next();
            AbstractBaseConfig baseConfig = null;
            switch (element.getQualifiedName()) {
                case Constants.ELEMENT_EXCEL_EXPORT:
                    baseConfig = new ExcelOfExportVo();
                    break;
                case Constants.ELEMENT_EXCEL_IMPORT:
                    baseConfig = new ExcelOfImportVo();
                    break;
                case Constants.ELEMENT_EXCEL_MAP:
                    baseConfig = new ExcelMap();
                    break;
                case Constants.ELEMENT_STYLE:
                    baseConfig = new CellStyleVo();
                    break;
                case Constants.ELEMENT_FONT:
                    baseConfig = new FontVo();
                    break;
                default:
                    break;
            }
            // setProperty(element, baseConfig);// 更新了方法名称
            configuration.getBaseConfigs().put(element.attributeValue("id"), baseConfig);
        }
    }

    /**
     * 检查 导入 的依赖关系，是否存在循环依赖（ERROR），或依赖没有对应的值（WARN）
     *
     * @param importVo 导入 的配置对象
     */
    @Deprecated
    private void checkDependencies(ExcelOfImportVo importVo) {
        // Map<String, Map<String, Set<String>>> dependencies = importVo.getDependencies();
        // TODO 将property转换为header
        // 这里可能会出现这种情况：property 存在 setterELs 中，而具体的 property 只有在读取 excel 时才能发现其是否成立，如果判断不成立，则会导致 property 缺失；
        // TODO 使用 LinkedList 存储依赖关系，被依赖者在前，依赖者在后
        // LinkedList 不适用，因为一个 ExcelEntry 可能依赖多个 property
        // 取消泛型，让 LinkedList 可以存储 LinkedList 也可以存储 String
        // LinkedList linkedList = new LinkedList();
    }

    /**
     * 解析 导入 时的依赖关系
     *
     * @param importVo 导入对应的配置对象
     */
    private void parseDependencies(ExcelOfImportVo importVo) {
        // 处理 entry 之间的依赖关系
        Map<String, Map<String, Set<String>>> dependencies = new HashMap<>();// 依赖
        // 循环所有的 excelEntry
        // OgnlContext context = new OgnlContext();// 不需要 context
        List<SheetVo> sheetVos = importVo.getSheetVos();
        for (SheetVo sheetVo : sheetVos) {
            for (Map.Entry<String, ExcelEntry> entry : sheetVo.getExcelEntryMap().entrySet()) {
                ExcelEntry excelEntry = entry.getValue();
                // 当前 excelEntry 使用的是 if ，而不是直接指定 setter
                Map<String, String> setterELs = excelEntry.getSetterELs();
                if (CommonUtil.isEmpty(excelEntry.getSetter()) && setterELs != null && !setterELs.isEmpty()) {
                    Map<String, Set<String>> dependInfoMap = new HashMap<>();// 当前表头在判断时所依赖的其他表头的值
                    for (String condition : setterELs.keySet()) {
                        // 通过 ognl 表达式解析出 依赖的属性
                        try {
                            SimpleNode parsedExpression = (SimpleNode) Ognl.parseExpression(condition);
                            // iterateExpressionNode(context, parsedExpression, dependInfoMap);
                            iterateExpressionNode(condition, parsedExpression, dependInfoMap);
                        } catch (OgnlException e) {
                            e.printStackTrace();
                        }
                    }
                    excelEntry.setDependencies(dependInfoMap);
                    dependencies.put(excelEntry.getHeader(), dependInfoMap);
                }
            }
            ((SheetForImportVo) sheetVo).setDependencies(dependencies);
        }
    }

    // 循环 所有的 children
    // private void iterateExpressionNode(OgnlContext context, SimpleNode parsedExpression, Map<String, String> dependInfoMap) throws OgnlException {
    private void iterateExpressionNode(String condition, SimpleNode parsedExpression, Map<String, Set<String>> dependInfoMap) throws OgnlException {
        // 找出所有的依赖属性，如:找出(objA.propertyB != null && objA.propertyC !='') 中的 objA.propertyB 和 objA.propertyC
        // ((ASTConst)((ExpressionNode)Ognl.parseExpression("a==1||b!=2&&c==true")).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0)).getValue()
        // 最底层的类是：ASTConst，ASTProperty 下还包含了一个 ASTConst
        Set<String> set = new HashSet<>();
        for (int i = 0; i < parsedExpression.jjtGetNumChildren(); i++) {
            Node node = parsedExpression.jjtGetChild(i);
            if (node instanceof SimpleNode) {
                SimpleNode simpleNode = (SimpleNode) node;
                // if (simpleNode.isNodeSimpleProperty(context) || simpleNode.isSimpleNavigationChain(context)) {
                if (simpleNode.isNodeSimpleProperty(null) || simpleNode.isSimpleNavigationChain(null)) {
                    // 属性，直接通过 toString 方法就可以获得
                    // dependInfoMap.put(condition, simpleNode.toString());
                    set.add(simpleNode.toString());
                } else if (!simpleNode.isNodeConstant(null)) {
                    // } else if (!simpleNode.isNodeConstant(context)) {
                    //     iterateExpressionNode(context, simpleNode, dependInfoMap);
                    iterateExpressionNode(condition, simpleNode, dependInfoMap);
                }
            }
        }
        dependInfoMap.put(condition, set);
    }

    /**
     * 用于sheet
     *
     * @param element
     * @param baseConfig
     * @author huanghao
     * @date 2017年4月7日下午5:22:47
     */
    private void setComplexProperty(Element element, AbstractBaseConfig baseConfig) {
        // Class<? extends AbstractBaseConfig> clz = baseConfig.getClass();
        // 标签内属性
        readAttr2Obj(element, baseConfig);
        // 子标签属性
        for (Iterator iterator = element.elementIterator(); iterator.hasNext(); ) {
            Element childElement = (Element) iterator.next();
            switch (childElement.getQualifiedName()) {
                case "header":

                    break;
                case "datas":

                    break;
                case "map":
                    readMap2Obj(baseConfig, childElement);
                    break;
                case "propertiesForTemplate":
                    readPropertiesForTemplate2Obj(baseConfig, childElement);
                    break;
                case "property":
                    readProperty2Obj(baseConfig, childElement);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 用于读取 excelImport或excelExport中的map
     *
     * @param baseConfig
     * @param childElement
     * @author huanghao
     * @date 2017年4月10日上午9:23:20
     */
    private void readMap2Obj(AbstractBaseConfig baseConfig, Element childElement) {
        CommonUtil.assertTrue("map".equals(childElement.getQualifiedName()), "当前元素不是 map 元素");
        CommonUtil.assertTrue(baseConfig instanceof SheetVo, "目前只有导入导出支持使用map节点，请检查xml配置" + childElement.asXML());
        SheetVo sheetVo = (SheetVo) baseConfig;
        Map<String, ExcelEntry> entryMap = sheetVo.getExcelEntryMap();
        for (Iterator elementIterator = childElement.elementIterator("entry"); elementIterator.hasNext(); ) {
            Element entry = (Element) elementIterator.next();
            // 对每一个 entry 进行处理
            Attribute attribute = entry.attribute("header");
            if (attribute != null) {
                // 先从map中获取，如果已经有了，则覆写
                ExcelEntry mappedEntry = entryMap.get(attribute.getStringValue());
                if (mappedEntry == null) {
                    mappedEntry = new ExcelEntry();
                }
                // 读取 entry 的属性
                readAttr2Obj(entry, mappedEntry);
                // 读取 entry 的子节点：遍历 entry 节点内部
                readChildNode2Obj(entry, mappedEntry);
                entryMap.put(mappedEntry.getHeader(), mappedEntry);
                mappedEntry.setSheetVo(sheetVo);
            }
            // 没有 header 就没有读取此 entry
        }
    }

    private void readPropertiesForTemplate2Obj(AbstractBaseConfig baseConfig, Element childElement) {
        CommonUtil.assertTrue("propertiesForTemplate".equals(childElement.getQualifiedName()), "当前节点不是 propertiesForTemplate 节点");
        // CommonUtil.assertTrue(baseConfig instanceof ExcelVo, "配置对象不为 ExcelVo 对象，而 propertiesForTemplate 只适用于 ExcelVo 对象");
        CommonUtil.assertTrue(baseConfig instanceof SheetVo, "配置对象不为 SheetVo 对象，而 propertiesForTemplate 只适用于 SheetVo 对象");
        SheetVo sheetVo = (SheetVo) baseConfig;
        String startIndexForTemplate = sheetVo.getStartIndexForTemplate();
        String startIndex = CommonUtil.isNotEmpty(startIndexForTemplate) ? startIndexForTemplate : "A";
        Boolean isConsecutive = sheetVo.getIsConsecutive();
        List<Integer> ignoreColumnIndex = CommonUtil.getIgnoreColumnIndex(sheetVo.getIgnoreColumnIndex());
        Map<String, ExcelEntry> entryMap = sheetVo.getExcelEntryMap();
        String mappedEntryKey = null;
        int index = CoreUtil.getColumnIndexFormExcelLocation(startIndex);// 这是开始设置值的那个索引
        // 处理每一个 entry 如果手动设置了 index，则直接输入，否则根据 startIndex 来计算
        for (Iterator elementIterator = childElement.elementIterator("entry"); elementIterator.hasNext(); index++) {
            if (ignoreColumnIndex.contains(index)) {
                continue;
            }
            Element entry = (Element) elementIterator.next();
            // 对每一个 entry 进行处理 -> 流程：是否手动设置了 cellIndex，复制属性，复制子标签属性
            Attribute attribute = entry.attribute("cellIndex");
            if (attribute != null) {
                mappedEntryKey = attribute.getStringValue();
            } else {
                // 检查 startIndex 与 isConsecutive
                if (Boolean.TRUE.equals(isConsecutive)) {
                    mappedEntryKey = String.valueOf(index);
                } else {
                    CommonUtil.throwArgument("当前" + sheetVo.getId() + "的列(" + entry.getStringValue() + ")找不到对应的索引值，请确认是否设置正确");
                }
            }
            // 先从map中获取，如果已经有了，则覆写
            ExcelEntry mappedEntry = entryMap.get(mappedEntryKey);
            if (mappedEntry == null) {
                mappedEntry = new ExcelEntry();
            }
            // 设置 cellIndex
            getOrSet2Obj(mappedEntry, "columnIndex", mappedEntryKey, Boolean.TRUE);
            // 读取 entry 的属性
            readAttr2Obj(entry, mappedEntry);
            // 读取 entry 的子节点：遍历 entry 节点内部
            readChildNode2Obj(entry, mappedEntry);
            entryMap.put(mappedEntryKey, mappedEntry);
            mappedEntry.setSheetVo(sheetVo);
            // 没有 templateLocation 就没有读取此 entry
        }
    }

    /**
     * 用于&lt;excelMap&gt;的配置
     *
     * @param element
     * @param baseConfig
     * @author huanghao
     * @date 2017年4月7日下午5:23:07
     */
    private void setMappingProperty(Element element, AbstractBaseConfig baseConfig) {
        // Class<? extends AbstractBaseConfig> clz = baseConfig.getClass();
        // 标签内属性
        readAttr2Obj(element, baseConfig);
        // 子标签属性
        for (Iterator iterator = element.elementIterator(); iterator.hasNext(); ) {
            Element childElement = (Element) iterator.next();
            switch (childElement.getQualifiedName()) {
                case "mapping":
                    setMapping2ExcelMap(baseConfig, childElement);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 用于excelMap和font,style
     *
     * @param element
     * @param baseConfig
     * @author huanghao
     * @date 2017年4月7日下午5:23:07
     */
    private void setEasyProperty(Element element, AbstractBaseConfig baseConfig) {
        // Class<? extends AbstractBaseConfig> clz = baseConfig.getClass();
        // 标签内属性
        readAttr2Obj(element, baseConfig);
        // 子标签属性
        for (Iterator iterator = element.elementIterator(); iterator.hasNext(); ) {
            Element childElement = (Element) iterator.next();
            switch (childElement.getQualifiedName()) {
                case "property":
                    readProperty2Obj(baseConfig, childElement);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 映射 mapping 关系（mapping(property effectiveHeader)）
     *
     * @param baseConfig
     * @param childElement
     * @author huanghao
     * @date 2017年4月7日下午5:37:45
     */
    private void setMapping2ExcelMap(AbstractBaseConfig baseConfig, Element childElement) {
        if (Constants.ELEMENT_EXCEL_MAP.equals(childElement.getParent().getQualifiedName())) {
            String property = null;
            String effectiveHeader = null;
            Attribute nameAttr = childElement.attribute("property");
            if (nameAttr != null && CommonUtil.isNotEmpty(nameAttr.getStringValue())) {
                property = nameAttr.getStringValue();
            }
            Attribute valueAttr = childElement.attribute("effectiveHeader");
            if (valueAttr != null && CommonUtil.isNotEmpty(valueAttr.getStringValue())) {
                effectiveHeader = valueAttr.getStringValue();
            } else if (effectiveHeader == null && childElement.isTextOnly()) {
                effectiveHeader = childElement.getData().toString();
            }
            if (CommonUtil.isNotEmpty(effectiveHeader) && CommonUtil.isNotEmpty(property)) {
                ((ExcelMap) baseConfig).getMapper().put(effectiveHeader, property);
            }
        }
    }

    /**
     * 读取 entry 节点的子节点，并设置到 mappedEntry 中
     *
     * @param entry
     * @param mappedEntry
     */
    private void readChildNode2Obj(Element entry, ExcelEntry mappedEntry) {
        for (Iterator childOfEntryIterator = entry.elementIterator(); childOfEntryIterator.hasNext(); ) {
            Element childOfEntry = (Element) childOfEntryIterator.next();// getter、setter
            String attrName = childOfEntry.getQualifiedName();
            Object value;
            if (childOfEntry.isTextOnly()) {
                value = childOfEntry.getTextTrim();
            } else {
                attrName += "ELs";
                value = readValueElement2Obj(childOfEntry, mappedEntry);
            }
            getOrSet2Obj(mappedEntry, attrName, value, Boolean.TRUE);
        }
    }

    /**
     * 将读取到element的attr设置到对象中
     *
     * @param element 要读取attr的元素节点
     * @param obj     要设置属性到此对象中
     * @author huanghao
     * @date 2017年4月7日下午5:19:44
     */
    private void readAttr2Obj(Element element, Object obj) {
        for (Iterator iterator = element.attributeIterator(); iterator.hasNext(); ) {
            Attribute attribute = (Attribute) iterator.next();
            String qualifiedName = attribute.getQualifiedName();
            getOrSet2Obj(obj, qualifiedName, attribute.getStringValue(), Boolean.TRUE);
        }
    }

    /**
     * 获取最终取值,一般可能会有一个判断标签，如：if
     * <p>
     * 只能解析出相应的表达式，在读取excel的时候再进行判断
     *
     * @param element
     * @param obj
     * @return
     * @author huanghao
     * @date 2017年4月18日下午2:07:49
     */
    private Object readValueElement2Obj(Element element, Object obj) {
        // List<Map<String, String>> value = new ArrayList<>();
        Map<String, String> conditionValue = new HashMap<>();
        for (Iterator iterator = element.elementIterator(); iterator.hasNext(); ) {
            Element childElement = (Element) iterator.next();
            String qualifiedName = childElement.getQualifiedName();
            switch (qualifiedName) {
                case "if":
                    putConditionValueFromIf(childElement, conditionValue);
                    break;
                case "else":
                    // value = getValueFromIf(childElement);
                    break;
                case "choose":
                    // (choose -> (when , otherwise))
                    // value = getValueFromIf(childElement);
                    break;
                default:
                    break;
            }
            // getOrSet2Obj(obj, qualifiedName, childElement.getStringValue(),
            // Boolean.TRUE);
        }
        return conditionValue;
    }

    /**
     * 将if标签中的test和内容设置到指定的map中
     *
     * @param childElement
     * @param conditionValue
     * @author huanghao
     * @date 2017年4月19日下午4:04:28
     */
    private void putConditionValueFromIf(Element childElement, Map<String, String> conditionValue) {
        Attribute attribute = childElement.attribute("test");
        CommonUtil.assertArgumentNotNull(attribute, "if 标签中没有检测到 test 属性");
        String testAttrValue = attribute.getStringValue();
        CommonUtil.assertArgumentNotEmpty(testAttrValue, "if 标签中 test 属性值不合法('" + testAttrValue + "')");
        if (!childElement.isTextOnly()) {
            throw new IllegalArgumentException("if 标签内部应该只包含文本内容");
        }
        String textTrim = childElement.getTextTrim();
        conditionValue.put(testAttrValue, textTrim);
    }

    /**
     * 读取 property(name->value/ref型) 子标签
     *
     * @param baseConfig   要设置属性的对象
     * @param childElement property Element
     * @author huanghao
     * @date 2017年4月7日下午3:11:24
     */
    private void readProperty2Obj(AbstractBaseConfig baseConfig, Element childElement) {
        String name = null;
        String value = null;
        Attribute nameAttr = childElement.attribute("name");
        if (nameAttr != null && CommonUtil.isNotEmpty(nameAttr.getStringValue())) {
            name = nameAttr.getStringValue();
        }
        Attribute valueAttr = childElement.attribute("value");
        // ref 为对象的引用，应该单独处理
        Attribute refAttr = childElement.attribute("ref");
        // 优先级 高valueAttr -> refAttr -> text低
        if (valueAttr != null && CommonUtil.isNotEmpty(valueAttr.getStringValue())) {
            value = valueAttr.getStringValue();
        } else if (refAttr != null && CommonUtil.isNotEmpty(refAttr.getStringValue())) {
            name += "Ref";
            value = refAttr.getStringValue();
        } else if (value == null && childElement.isTextOnly()) {
            value = childElement.getData().toString();
        } else {
            // TODO 判断property标签之间的内容，如果包含其他属性，如：单独的 ref

        }
        if (CommonUtil.isNotEmpty(name)) {
            getOrSet2Obj(baseConfig, name, value, Boolean.TRUE);
        }
    }


    /**
     * 获取或设置属性到对象上
     *
     * @param obj       指定的对象
     * @param attrName  属性名
     * @param attrValue 属性值
     * @param isSetter  是否是setter方法
     * @return 如果是get，则返回获取到的值
     * @author huanghao
     * @date 2017年4月7日上午10:16:10
     */
    private Object getOrSet2Obj(Object obj, String attrName, Object attrValue, Boolean isSetter) {
        // 如果属性名为空，则跳过
        if (CommonUtil.isNotEmpty(attrName)) {
            try {
                PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(obj, attrName);
                Method tempWriteMethod = null;
                Method writeMethod = propertyDescriptor.getWriteMethod();
                if (writeMethod == null) {
                    tempWriteMethod = obj.getClass().getMethod(CommonUtil.getSetter(attrName, Boolean.TRUE), String.class);
                }
                Method readMethod = propertyDescriptor.getReadMethod();
                if (readMethod == null) {
                    propertyDescriptor.setReadMethod(obj.getClass().getMethod(CommonUtil.getSetter(attrName, Boolean.FALSE)));
                }
                Method method = Boolean.TRUE.equals(isSetter)
                        ? (writeMethod == null ? tempWriteMethod : writeMethod)
                        : readMethod;
                return method.invoke(obj, attrValue);
            } catch (NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(
                        "没有找到属性（'" + attrName + "'）的 " + (Boolean.TRUE.equals(isSetter) ? "setter" : "getter") + " 方法");
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
