/*
 * Copyright [2025] [Surpass of copyright http://www.surpass.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */






package org.dromara.surpass.entity.history;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.mybatis.jpa.entity.JpaEntity;

/**
 * .
 * @author Crystal.Sea
 *
 */

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Table(name = "surpass_history_system_logs")
public class HistorySystemLogs extends JpaEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 6560201093784960493L;

    @Id
    @Column
    @GeneratedValue
    String id;

    @Column
    String topic;

    @Column
    String message;

    @Column
    String messageAction;

    @Column
    String messageResult;

    @Column
    String targetId;

    @Column
    String targetName;

    @Column
    String cipherText;

    @Column
    String userId;

    @Column
    String username;

    @Column
    String displayName;

    @Column
    Date executeTime;

	String jsonCotent;

	private String instName;

	Date startDate;

	Date endDate;

}
