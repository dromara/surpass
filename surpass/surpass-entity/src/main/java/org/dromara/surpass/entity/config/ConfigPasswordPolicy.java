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






package org.dromara.surpass.entity.config;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.mybatis.jpa.entity.JpaEntity;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Crystal.Sea
 *
 */

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Table(name = "surpass_config_password_policy")
public class ConfigPasswordPolicy extends JpaEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -4797776994287829182L;

    @Id
    @Column
    @GeneratedValue
    String id;

    /**
     * minimum password lengths
     */

    @NotNull
    @Column
    private int minLength;

    /**
     * maximum password lengths
     */
    @Column
    @NotNull
    private int maxLength;

    /**
     * least lowercase letter
     */
    @Column
    @NotNull
    private int lowerCase;

    /**
     * least uppercase letter
     */
    @Column
    @NotNull
    private int upperCase;

    /**
     * inclusion of numerical digits
     */
    @Column
    @NotNull
    private int digits;

    /**
     * inclusion of special characters
     */
    @Column
    @NotNull
    private int specialChar;

    /**
     * require users to change passwords periodically
     */
    @Column
    private int expiration;

    /**
     * 0 no 1 yes
     */
    @Column
    private int username;

    /**
     * not include password list
     */
    @Column
    private int history;

    @Column
    private int dictionary;

    @Column
    private int alphabetical;

    @Column
    private int numerical;

    @Column
    private int qwerty;

    @Column
    private int occurances;

    private int randomPasswordLength = 12;

    List<String> policMessageList;
}
