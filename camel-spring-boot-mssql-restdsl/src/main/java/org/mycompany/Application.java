/**
 *  Copyright 2005-2018 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package org.mycompany;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * A spring-boot application that includes a Camel route builder to setup the Camel routes
 */
@SpringBootApplication
@ImportResource({"classpath:spring/camel-context.xml"})
public class Application {

    // must have a main method spring-boot can run
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
 
    /*    @Component
    class Backend extends RouteBuilder {

        @Override
        public void configure() {
            // A first route generates some orders and queue them in DB
            from("timer:new-order?repeatCount=1")
                .routeId("generate-order")
                .to("sql:SELECT TOP 1 NAM_LAST_KY, NAM_FIRST_KY, DTE_BIRTH_KY, NBR_SSN_LAST4_KY,NBR_CASE_KY, CDE_CATEGORY_KY, NBR_SEQ_PROG_KY, SEGMENT_PARTNO, SEGMENT_SEQN_NUM, DTE_SEGMENT, CDE_STAT_AG, DTE_STAT_EFF, DTE_REAPP_DUE, DTE_FS_SR_DUE, DTE_APPT, TME_APPT, CDE_TYP_APPT, AMT_BNFT, CDE_BNFT_STATUS, DTE_BNFT_STATUS, PROG_UPDATE FROM DW_ODS_CaseConnect.dbo.GSW1CASE   ORDER BY NAM_LAST_KY?" +
                    "dataSource=dataSource")
                .log("Inserted new order ${body.id}");

            // A second route polls the DB for new orders and processes them
            from("sql:select * from orders where processed = false?" +
                "consumer.onConsume=update orders set processed = true where id = :#id&" +
                "consumer.delay={{quickstart.processOrderPeriod:5s}}&" +
                "dataSource=dataSource")
                .routeId("process-order")
                .bean("orderService", "rowToOrder")
                .log("Processed order #id ${body.id} with ${body.amount} copies of the «${body.description}» book");
        }
    }*/

}
