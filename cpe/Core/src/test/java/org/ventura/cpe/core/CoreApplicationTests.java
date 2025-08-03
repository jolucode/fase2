package org.ventura.cpe.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CoreApplicationTests {
//
//    String input = ".xml";
//
//    String output = "db/changelog/db.changelog-master.yaml";

    @Test
    public void contextLoads() {
    }
//
//    @Test
//    public void contextLoads() throws LiquibaseException, IOException {
//        ClassPath classPath = new ClassPath();
//        ClassPathLoader classPathLoader = new ClassPathLoader(classPath);
//        ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(classPathLoader);
//        DatabaseChangeLog changeLog = ChangeLogParserFactory.getInstance().getParser(".xml", classLoaderResourceAccessor)
//                .parse("db/changelog/master.xml", new ChangeLogParameters(), new ClassLoaderResourceAccessor());
////
////
//        Resource resource = new FileSystemResource("D:\\Trabajo\\Ventura\\FE_Ventura\\Core\\src\\main\\resources\\db\\changelog\\db.changelog-master.yaml");
//        ChangeLogSerializerFactory instance = ChangeLogSerializerFactory.getInstance();
//        ChangeLogSerializer serializer = instance.getSerializer(".yaml");
//        OutputStream ymlOutputstream = Files.newOutputStream(Paths.get(resource.getURI()));
//        serializer.write(changeLog.getChangeSets(), ymlOutputstream);
//
//
////        ResourceAccessor resourceAccessor = new FileSystemResourceAccessor();
////        ChangeLogParser parser = null;
//        //            parser = ChangeLogParserFactory.getInstance().getParser(FilenameUtils.getExtension(input), resourceAccessor);
////            DatabaseChangeLog changeLog = parser
////                    .parse(input, new ChangeLogParameters(), resourceAccessor);
//
////        ChangeLogSerializer serializer = ChangeLogSerializerFactory.getInstance().getSerializer(FilenameUtils.getExtension(output));
////        try (OutputStream ymlOutputstream = Files.newOutputStream(Paths.get(resource.getURI()))) {
////            List<ChangeSet> changeSets = changeLog.getChangeSets();
////            serializer.write(changeSets, ymlOutputstream);
////        } catch (IOException e) {
////            throw new RuntimeException("Unable to write output file " + output, e);
////        }
//    }
}
