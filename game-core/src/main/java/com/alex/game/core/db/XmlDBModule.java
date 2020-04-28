/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.core.db;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.io.Resources;
import org.mybatis.guice.XMLMyBatisModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.inject.PrivateModule;

/**
 * 数据库模块,使用xml配置的mybatise模块
 *
 * @author Alex
 * @date 2016/7/28 17:22
 */
public class XmlDBModule extends PrivateModule {

	protected final Logger LOG = LoggerFactory.getLogger(getClass());

	// 游戏数据数据库jdbc配置文件配置信息
	private final Properties jdbcProps = new Properties();
	private final String environmentId;
	// 游戏数据mybatise的xml文件配置路径
	private final String xmlCfgPath;

	public XmlDBModule(String jdbcPath, String environmentId, String xmlCfgPath) {
		this.environmentId = environmentId;
		this.xmlCfgPath = xmlCfgPath;
		try (FileReader jdbcReader = new FileReader(jdbcPath)) {
			jdbcProps.load(jdbcReader);
		} catch (IOException e) {
			throw new RuntimeException("读取jdbc配置文件[" + jdbcPath + "]错误", e);
		}
	}

	@Override
	protected void configure() {
		install(new XMLMyBatisModule() {
			@Override
			protected void initialize() {
				setEnvironmentId(environmentId);
				setClassPathResource(xmlCfgPath);
				addProperties(jdbcProps);
			}
		});

		// expose所有的mapper接口
		exposeAllMappers();
	}

	/**
	 * expose所有的mapper接口，无法从XMLMyBatisModule拿到mapper，再解析一次xml
	 */
	private void exposeAllMappers() {
		try {
			Document doc = createDocument();
			XPathFactory pathFactory = XPathFactory.newInstance();
			XPath xpath = pathFactory.newXPath();

			Node root = (Node) xpath.evaluate("/configuration", doc, XPathConstants.NODE);
			NodeList mappers = ((Node) xpath.evaluate("mappers", root, XPathConstants.NODE)).getChildNodes();
			for (int i = 0; i < mappers.getLength(); i++) {
				Node node = mappers.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					String resource = node.getAttributes().getNamedItem("resource").getNodeValue();
					String mapperClassName = resource.replace('/', '.').replace(".xml", "");
					expose(Class.forName(mapperClassName));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("expose xml文件[" + xmlCfgPath + "]  mapper 接口异常", e);
		}
	}

	/**
	 * 创建Document
	 *
	 * @return
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private Document createDocument() throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(false);
		factory.setIgnoringComments(true);
		factory.setIgnoringElementContentWhitespace(false);
		factory.setCoalescing(false);
		factory.setExpandEntityReferences(true);

		DocumentBuilder builder = factory.newDocumentBuilder();
		// 使用mybatise本地dtd文件校验(默认会从网上下载dtd文件校验)
		builder.setEntityResolver(new XMLMapperEntityResolver());

		return builder.parse(Resources.getResourceAsStream(xmlCfgPath));
	}

}
