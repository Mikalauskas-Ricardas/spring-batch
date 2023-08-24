package com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.batch;

import com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.model.migration.postgresql.entity.Student;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.persistence.EntityManagerFactory;

@Configuration
public class MigrationJob {

    @Bean
    public Job studentsJob(JobBuilderFactory jobBuilderFactory,
                           Step migrationStep) {
        return jobBuilderFactory.get("Migration Job")
                .incrementer(new RunIdIncrementer())
                .start(migrationStep)
                .build();
    }
    @Bean
    public Step migrationStep(StepBuilderFactory stepBuilderFactory,
                              JpaCursorItemReader<Student> jpaItemReader,
                              ItemProcessor<Student, com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.model.migration.mysql.entity.Student> itemProcessor,
                              JpaItemWriter<com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.model.migration.mysql.entity.Student> jpaItemWriter,
                              JpaTransactionManager jpaTransactionManager) {
        return stepBuilderFactory.get("Migration Step")
                .<Student, com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.model.migration.mysql.entity.Student>chunk(3)
                .reader(jpaItemReader)
                .processor(itemProcessor)
                .writer(jpaItemWriter)
                .transactionManager(jpaTransactionManager)
                .build();

    }

    @Bean
    public JpaCursorItemReader<Student> jpaItemReader(EntityManagerFactory postgresqlEntityManagerFactory) {
        JpaCursorItemReader<Student> reader = new JpaCursorItemReader<>();
        reader.setEntityManagerFactory(postgresqlEntityManagerFactory);
        reader.setQueryString("FROM Student");

        return reader;
    }

    @Bean
    public JpaItemWriter<com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.model.migration.mysql.entity.Student> jpaItemWriter(EntityManagerFactory mysqlEntityManagerFactory) {
        JpaItemWriter<com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.model.migration.mysql.entity.Student> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(mysqlEntityManagerFactory);

        return writer;
    }

    @Bean
    public ItemProcessor<Student, com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.model.migration.mysql.entity.Student> itemProcessor() {
        return new ItemProcessor<Student, com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.model.migration.mysql.entity.Student>() {
            @Override
            public com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.model.migration.mysql.entity.Student process(Student student) throws Exception {
                com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.model.migration.mysql.entity.Student newStudent = new com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.model.migration.mysql.entity.Student();

                newStudent.setId(student.getId());
                newStudent.setFirstName(student.getFirstName());
                newStudent.setLastName(student.getLastName());
                newStudent.setEmail(student.getEmail());
                newStudent.setDeptId(student.getDeptId());
                newStudent.setIsActive(student.getIsActive().equals("true") ? true: false);

                return newStudent;
            }
        };
    }

}
