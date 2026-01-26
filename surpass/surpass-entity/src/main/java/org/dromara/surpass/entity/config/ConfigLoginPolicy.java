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
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.mybatis.jpa.entity.JpaEntity;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Crystal.Sea
 *
 */

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Table(name = "surpass_config_login_policy")
public class ConfigLoginPolicy extends JpaEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = 2563009899218736769L;

	@Id
	@Column
	@GeneratedValue
	String id;

	@NotNull
	@Column
	int sessionValidity;

	@Column
	@NotNull
	int tokenValidity;

	@Column
	@NotNull
	String isFirstPasswordModify;

	@Column
	@NotNull
	String captcha;

	@Column
	@NotNull
	String captchaMgt;

	@Column
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	int twoFactor;

	@Column
	@NotNull
	int loginAttempts;

	@Column
	@NotNull
	String isAutoLock;

	@Column
	@NotNull
	int lockInterval;

	@Column
	@NotNull
	int passwordAttempts;

	@Column
	@NotNull
	String passwordAttemptsCaptcha;

	@Column
	@NotNull
	String scanCode;

	@Column
	@NotNull
	String isMobile;

	@Column
	@NotNull
	String isSocial;

	@Column
	String redirectUri;

	@Column
	int terminals;

	public boolean isTwoFactor(){
		return this.twoFactor > 0;
	}

}
