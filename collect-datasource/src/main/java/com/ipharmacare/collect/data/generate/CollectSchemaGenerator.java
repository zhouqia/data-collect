package com.ipharmacare.collect.data.generate;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CollectSchemaGenerator {

    public static void main(String[] args){
        try{
            List<String> warnings = new ArrayList();
            boolean overwrite = true;
            URL url = com.ipharmacare.collect.data.generate.CollectSchemaGenerator.class.getClassLoader().getResource("generateConfig-collect.xml");
            File configFile = new File(url.getFile());
            ConfigurationParser cp = new ConfigurationParser(warnings);
            Configuration config = cp.parseConfiguration(configFile);
            DefaultShellCallback callback = new DefaultShellCallback(overwrite);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            myBatisGenerator.generate(null);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
