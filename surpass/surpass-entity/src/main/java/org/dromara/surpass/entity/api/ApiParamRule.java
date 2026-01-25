package org.dromara.surpass.entity.api;

import lombok.Data;

@Data
public class ApiParamRule {
    
    boolean required;
    
    Integer minLength;
    
    Integer maxLength;
    
    Long minValue;
    
    Long maxValue;
    
    String pattern;
    
    String format;
    
    String enumValues;
}
