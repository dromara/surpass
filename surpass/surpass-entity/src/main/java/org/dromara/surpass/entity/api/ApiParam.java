package org.dromara.surpass.entity.api;

import lombok.Data;

@Data
public class ApiParam {
    
    String name;
    
    String type;
    
    ApiParamRule rules;
    
    String description;
    
    boolean readOnly;
}
