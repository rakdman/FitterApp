package com.fittr.server.batch;


import com.fittr.server.model.Vouchers;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;



/**
 * This class written for uploading vouchers using a file
 *
 * @author Rakesh Kumar
 */


@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Bean
	public Job job(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
					   ItemReader<Vouchers> itemReader, ItemWriter<Vouchers> itemWriter) {

		Step step = stepBuilderFactory.get("Load-VOUCHE_RECORD-CSV-FILE").<Vouchers,Vouchers>chunk(100).reader(itemReader).writer(itemWriter).build();
		return jobBuilderFactory.get("Batch-JOB").start(step).build();
	}
	
	@Bean
	public FlatFileItemReader<Vouchers> itemReader(){
 
		FlatFileItemReader<Vouchers> flatFileItemReader = new FlatFileItemReader<>();
		flatFileItemReader.setResource(new FileSystemResource("input/voucherFile.csv"));
		flatFileItemReader.setName("CSV-File");
		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setLineMapper(lineMapper());
		
		return flatFileItemReader;
	}

	@Bean
	public LineMapper<Vouchers> lineMapper() {
		DefaultLineMapper<Vouchers> defaultLineMapper = new DefaultLineMapper<>();
		
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("voucherTitle","voucherDesc","voucherCoins");
		
		FieldSetMapper<Vouchers> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		((BeanWrapperFieldSetMapper<Vouchers>) fieldSetMapper).setTargetType(Vouchers.class);
		
		defaultLineMapper.setLineTokenizer(lineTokenizer);
		defaultLineMapper.setFieldSetMapper(fieldSetMapper);
		
		return defaultLineMapper;
	}
	
	

}
