package com.tyss.restdemo.config;

import com.tyss.restdemo.entity.Employee;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class EmployeeBatchConfig {

    @Bean
    public Job employeeBatchJob(JobRepository jobRepository, Step employeeBatchStep) {

        return new JobBuilder("employeeBatchJob", jobRepository)
                .start(employeeBatchStep)
                .build();
    }

    @Bean
    public Step employeeBatchStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            JpaPagingItemReader<Employee> employeeReader,
            ItemProcessor<Employee, Employee> employeeProcessor,
            ItemWriter<Employee> employeeWriter) {

        return new StepBuilder("employeeBatchStep", jobRepository)
                .<Employee, Employee>chunk(100)
                .transactionManager(transactionManager)
                .reader(employeeReader)
                .processor(employeeProcessor)
                .writer(employeeWriter)
                .build();
    }

    @Bean
    public JpaPagingItemReader<Employee> employeeReader(EntityManagerFactory entityManagerFactory) {

        JpaPagingItemReader<Employee> reader = new JpaPagingItemReader<>(entityManagerFactory);
        reader.setName("employeeReader");
        reader.setQueryString("SELECT e FROM Employee e ORDER BY e.id");
        reader.setPageSize(100);
        return reader;

    }

    @Bean
    public ItemProcessor<Employee, Employee> employeeProcessor() {
        return employee -> employee;
    }

    @Bean
    public JpaItemWriter<Employee> employeeWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriter<>(entityManagerFactory);
    }
}