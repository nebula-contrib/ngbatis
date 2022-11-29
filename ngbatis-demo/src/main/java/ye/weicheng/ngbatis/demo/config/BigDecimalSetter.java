package ye.weicheng.ngbatis.demo.config;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.vesoft.nebula.Value;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import org.nebula.contrib.ngbatis.binding.Setter;
import org.springframework.stereotype.Component;
import ye.weicheng.ngbatis.demo.annotations.ValueType;

/**
 * @author yeweicheng
 * @since 2022-11-29 20:38
 *    <br> Now is history!
 */
@Component
public class BigDecimalSetter implements Setter<BigDecimal> {

  @Override
  public Object set(BigDecimal param) {
    return param.doubleValue();
  }
  
  @Override
  public Object set(BigDecimal param, Field field) {
    ValueType annotation = field.getAnnotation(ValueType.class);
    Class<?> valueType = annotation.value();
    return valueType == Byte.class ? Value.iVal(param.byteValue())
      : valueType == Short.class ? Value.iVal(param.shortValue())
        : valueType == Integer.class ? Value.iVal(param.intValue())
          : valueType == Long.class ? Value.iVal(param.longValue())
            : valueType == Float.class ? Value.fVal(param.floatValue())
              : Value.fVal(param.doubleValue());
  }
}
