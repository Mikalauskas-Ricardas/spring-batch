package com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.batch;

import com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.batch.listener.ProcessorExceptionListener;
import com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.batch.listener.ReaderExceptionListener;
import com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.batch.listener.StepSkipListener;
import com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.batch.listener.WriterExceptionListener;
import com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.model.Student;
import com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.util.Utils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.*;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.*;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;
import java.io.*;
import java.util.Date;
import java.util.List;

@Configuration
public class SampleJob {

    // @Bean
    public Job studentsJob(JobBuilderFactory jobBuilderFactory,
                           Step studentsProcessStep) {
        return jobBuilderFactory.get("Students Job")
                .incrementer(new RunIdIncrementer())
                .start(studentsProcessStep)
                .build();
    }

    @Bean
    public Step studentsProcessStep(StepBuilderFactory stepBuilderFactory,
                                    FlatFileItemReader<Student> studentFlatFileItemReader,
                                    ItemProcessor<Student, Student> studentItemProcessor,
                                    ItemWriter<Student> consoleItemWriter,
                                    ReaderExceptionListener readerExceptionListener,
                                    ProcessorExceptionListener processorExceptionListener,
                                    WriterExceptionListener writerExceptionListener,
                                    StepSkipListener stepSkipListener) {
        return stepBuilderFactory.get("Students Process Step")
                .<Student, Student>chunk(5)
                .reader(studentFlatFileItemReader)
                .processor(studentItemProcessor)
                .writer(consoleItemWriter)
                // If some record throws any exception the entire job process will be stopped
                // to avoid this, it's possible to skip some exceptions and continue with
                // the job execution.
                .faultTolerant()
                .skip(FlatFileParseException.class)
                .skip(NullPointerException.class)
                //.skipLimit(10)
                //.skipPolicy(new AlwaysSkipItemSkipPolicy())
                .retryLimit(1)
                .retry(Throwable.class)
                //.listener(readerExceptionListener)
                //.listener(processorExceptionListener)
                //.listener(writerExceptionListener)
                .listener(stepSkipListener)
                .build();
    }

    @Bean
    public FlatFileItemReader<Student> studentFlatFileItemReader() {
        FlatFileItemReader<Student> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("students.csv"));

        DefaultLineMapper<Student> mapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("Id", "First name", "Last name", "Email");

        BeanWrapperFieldSetMapper<Student> wrapper = new BeanWrapperFieldSetMapper<>();
        wrapper.setTargetType(Student.class);

        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(wrapper);

        reader.setLineMapper(mapper);
        reader.setLinesToSkip(1);
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<Student> jdbcBatchItemWriter(DataSource universityDataSource) {
        JdbcBatchItemWriter<Student> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(universityDataSource);
        writer.setSql("INSERT INTO newstudent(id, first_name, last_name, email)" +
                "values (:id, :firstName, :lastName, :email)");

        BeanPropertyItemSqlParameterSourceProvider<Student> propertyProvider = new BeanPropertyItemSqlParameterSourceProvider();
        writer.setItemSqlParameterSourceProvider(propertyProvider);
        return writer;
    }

    // @Bean
    public JsonFileItemWriter<Student> jsonFileItemWriter() {
        File file = Utils.createTempFile(".json");
        JsonFileItemWriter<Student> writer = new JsonFileItemWriter<>(
                new FileSystemResource(file.getAbsolutePath()),
                new JacksonJsonObjectMarshaller<Student>()
        );

        return writer;

    }

    // @Bean
    public StaxEventItemWriter<Student> xmlFileItemWriter() {
        StaxEventItemWriter<Student> writer = new StaxEventItemWriter<>();

        File file = Utils.createTempFile(".xml");
        writer.setResource(new FileSystemResource(file.getAbsolutePath()));
        writer.setRootTagName("students");

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Student.class);

        writer.setMarshaller(marshaller);
        return writer;
    }

    // @Bean
    public FlatFileItemWriter<Student> flatFileItemWriter() {
        File file = Utils.createTempFile(".csv");
        FlatFileItemWriter<Student> writer = new FlatFileItemWriter<>();

        // BS. Use FileSystemResource instead of ClassPathResource because
        // temp directory cannot be reached with app context
        writer.setResource(new FileSystemResource(file.getAbsolutePath()));
        writer.setHeaderCallback(new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.write("Id, First name, Last name, Email");
            }
        });

        writer.setFooterCallback(new FlatFileFooterCallback() {
            @Override
            public void writeFooter(Writer writer) throws IOException {
                writer.write("Created @" + new Date());
            }
        });

        DelimitedLineAggregator delimitedLineAggregator = new DelimitedLineAggregator<>();

        BeanWrapperFieldExtractor fieldExtractor = new BeanWrapperFieldExtractor();
        fieldExtractor.setNames(new String[]{"id", "firstName", "lastName", "email"});

        delimitedLineAggregator.setFieldExtractor(fieldExtractor);
        writer.setLineAggregator(delimitedLineAggregator);

        return writer;
    }

    @Bean
    public ItemWriter<Student> consoleItemWriter() {
        return new ItemWriter<Student>() {
            @Override
            public void write(List<? extends Student> items) throws Exception {
                System.out.println("Writing new student chunk");
                items.forEach(item -> {
                    // To test WriterExceptionListener
                    if(item.getId() == 11) throw new NullPointerException();

                    System.out.println(item);
                });
            }
        };
    }

    @Bean
    public JdbcCursorItemReader<Student> jdbcCursorItemReader(DataSource universityDataSource) {
        JdbcCursorItemReader<Student> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(universityDataSource);
        reader.setSql("SELECT * FROM student");

        BeanPropertyRowMapper<Student> rowMapper = new BeanPropertyRowMapper<>();
        rowMapper.setMappedClass(Student.class);
        reader.setRowMapper(rowMapper);

        return reader;
    }

    @Bean
    public ItemProcessor<Student, Student> studentItemProcessor() {
        return new ItemProcessor<Student, Student>() {
            @Override
            public Student process(Student item) {

                // Test the ItemProcessorListener
                if(item.getId() == 10) throw new NullPointerException();

                item.setFirstName(item.getFirstName().toUpperCase());
                item.setLastName(item.getLastName().toUpperCase());
                item.setEmail(item.getEmail().toUpperCase());
                return item;
            }
        };
    }

}
