package com.vik.playground.jaamsim.batch;

import com.vik.playground.jaamsim.domain.MyItem;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public FlatFileItemReader<MyItem> reader() {
		return new FlatFileItemReaderBuilder<MyItem>()
			.name("myItemReader")
			.resource(new ClassPathResource("data.csv"))
			.delimited()
			.names("id", "runDuration")
			.linesToSkip(1)
			.lineMapper(lineMapper())
			.fieldSetMapper(new BeanWrapperFieldSetMapper<MyItem>() {{
				setTargetType(MyItem.class);
			}})
			.build();
	}

	@Bean
	public LineMapper<MyItem> lineMapper() {
		final DefaultLineMapper<MyItem> defaultLineMapper = new DefaultLineMapper<>();
		final DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("id", "runDuration");
		final MyItemFieldSetMapper fieldSetMapper = new MyItemFieldSetMapper();
		defaultLineMapper.setLineTokenizer(lineTokenizer);
		defaultLineMapper.setFieldSetMapper(fieldSetMapper);
		return defaultLineMapper;
	}

	@Bean
	public Step step1(MyItemProcessor myItemProcessor) {
		return stepBuilderFactory
			.get("step1")
			.<MyItem, MyItem> chunk(100)
			.reader(reader())
			.processor(myItemProcessor)
			.writer(new ItemWriter<MyItem>() {
				@Override
				public void write(List<? extends MyItem> items) throws Exception {

				}
			})
			.taskExecutor(new SimpleAsyncTaskExecutor())
			.build();
	}

	@Bean
	public Job importItemsJob(NotificationListener listener, Step step1) {
		return jobBuilderFactory
			.get("importItemJob")
			.incrementer(new RunIdIncrementer())
			.listener(listener)
			.flow(step1)
			.end()
			.build();
	}

}
