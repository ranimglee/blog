package com.blog.afaq.repository;

import com.blog.afaq.dto.response.MonthlyAccessStat;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomAccessLogRepositoryImpl implements CustomAccessLogRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<MonthlyAccessStat> countMonthlyAccesses(Instant fromDate) {
        MatchOperation match = Aggregation.match(Criteria.where("accessTime").gte(fromDate));

        ProjectionOperation project = Aggregation.project()
                .andExpression("year(accessTime)").as("year")
                .andExpression("month(accessTime)").as("month");

        GroupOperation group = Aggregation.group("year", "month").count().as("count");

        SortOperation sort = Aggregation.sort(Sort.Direction.ASC, "_id.year", "_id.month");

        Aggregation aggregation = Aggregation.newAggregation(match, project, group, sort);

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "access_logs", Document.class);

        List<MonthlyAccessStat> stats = new ArrayList<>();

        for (Document doc : results) {
            Document id = (Document) doc.get("_id");
            int year = id.getInteger("year");
            int month = id.getInteger("month");

            Number countNumber = (Number) doc.get("count"); // <-- Fix here
            long count = countNumber.longValue();

            String formattedMonth = String.format("%d-%02d", year, month);
            stats.add(new MonthlyAccessStat(formattedMonth, count));
        }


        return stats;
    }
}
