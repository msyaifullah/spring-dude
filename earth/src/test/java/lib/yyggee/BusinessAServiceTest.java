package lib.yyggee;

import lib.yyggee.domain.model.Thing;
import lib.yyggee.domain.rules.*;
import lib.yyggee.domain.services.BusinessAService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;


@Slf4j
@ActiveProfiles("gitlab")
class BusinessAServiceTest {
    @BeforeAll
    public static void init() {

    }

    @BeforeEach
    void injectData() {

    }

    @Test
    public void findAllTest_WhenSomething() {
        AggregateSource data = new AggregateSource();
        String res = data.getData("satu", "dua");

        assertThat("satudua", is("satudua"));

        log.info("this is the data tested {}", res);

    }

    @Test
    public void findAllTest_WhenNoRecord() {

        //logic testing
        IRule logic = Rule.and(
                new RuleProblemA("1"),
                Rule.or(
                        new RuleProblemB("2"),
                        new RuleProblemB("3"),
                        new RuleProblemB("4"),
                        new RuleProblemC("5"),
                        new RuleProblemB("6"),
                        new RuleProblemA("7")
                ),
                new RuleProblemB("8"),
                new RuleProblemA("9"),
                Rule.and(
                        new RuleProblemA("10"),
                        new RuleProblemB("11")
                )
        );

        Thing thing = new Thing();
        Boolean data = new BusinessAService().evaluate(logic, thing);
        log.info("this is the data tested {}", data);
    }

    @Test
    public void generate_PaymentConfig() {
        Map<String, String> dataConverted = new HashMap<>();
        List<Thing> data = new ArrayList<>(){{
            add(new Thing(1L, "nameOne", new BigDecimal("30.901")));
            add(new Thing(2L, "nameTwo", new BigDecimal("30.902")));
            add(new Thing(3L, "nameThree", new BigDecimal("30.903")));
        }};

        dataConverted = data.stream().collect(Collectors.toMap(thing -> thing.getAmount().toString(), Thing::getName));

        log.info("dataConverted Sample {}", dataConverted);

        dataConverted = new HashMap<>(){{
            put("1", "nameOne");
            put("2", "nameTwo");
            put("3", "nameThree");
        }};

        data = new ArrayList<>();
        for (Map.Entry<String, String> entry : dataConverted.entrySet()) {
            data.add(new Thing(Long.parseLong(entry.getKey()), entry.getValue(), new BigDecimal("0")));
        }

        log.info("data Sample {}", data);

    }


}
