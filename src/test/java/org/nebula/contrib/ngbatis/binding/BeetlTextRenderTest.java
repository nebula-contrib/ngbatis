package org.nebula.contrib.ngbatis.binding;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import com.alibaba.fastjson.JSON;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nebula.contrib.ngbatis.config.ParseCfgProps;

/**
 * @author yeweicheng
 * @since 2022-06-21 2:56 <br>
 *     Now is history!
 */
class BeetlTextRenderTest {

  private BeetlTextRender render = new BeetlTextRender();

  @BeforeEach
  public void setRenderProp() {
    render.setProps(new ParseCfgProps());
  }

  @Test
  public void test() {
    String text =
        "        MATCH (n:${ tag })\n"
            + "        WHERE\n"
            + "         @if ( isNotEmpty (columns)  ) {\n"
            + "           @for ( col in columns ) {\n"
            + "               n.${ tag }.${ col } == $${ col }\n"
            + "           @}\n"
            + "        @}\n"
            + "        LIMIT 4000";
    String cql =
        render.resolve(
            text,
            new HashMap<String, Object>() {
              {
                put("columns", Arrays.asList("name"));
                put("tag", "person");
              }
            });
    System.out.println(cql);
  }

  @Test
  public void testNull() {
    String text = "${ nvl( name, 'null' ) }";
    String cql =
        render.resolve(
            text,
            new HashMap<String, Object>() {
              {
                put("name", null);
              }
            });
    System.out.println(cql);
  }

  @Test
  public void test2() {
    String text =
        "        MATCH (n:${ tag })\n"
            + "        @if ( isNotEmpty(columns) ) {\n"
            + "            WHERE\n"
            + "            @for ( col in columns ) {\n"
            + "                n.${ tag }.${ col } == ${ @valueColumns.get( colLP.index - 1 ) }\n"
            + "                @if (!colLP.last) {\n"
            + "                    and\n"
            + "                @}\n"
            + "            @}\n"
            + "        @}\n"
            + "        RETURN n\n"
            + "        LIMIT 4000";
    String cql =
        render.resolve(
            text,
            new HashMap<String, Object>() {
              {
                put("columns", Arrays.asList("name"));
                put("valueColumns", Arrays.asList("'$name'"));
                put("tag", "person");
              }
            });
    System.out.println(cql);
  }

  @Test
  public void testIfElse() {
    String text =
        "            MATCH (n: person)\n"
            + "            WHERE 1 == 1 \n"
            + "            @if ( gender == 'F' ) {\n"
            + "              AND n.person.age >= 20\n"
            + "            @}else{\n"
            + "              AND n.person.age >= 22\n"
            + "            @}\n"
            + "            RETURN n\n"
            + "            LIMIT 1";
    String cql =
        render.resolve(
            text,
            new HashMap<String, Object>() {
              {
                put("gender", "F");
              }
            });
    System.out.println(cql);
  }

  @Test
  public void testSwitchCase() {
    String text =
        "            MATCH (n: person)\n"
            + "            WHERE 1 == 1 \n"
            + "            @switch( gender ){\n"
            + "            @   case 'F':\n"
            + "                    AND n.person.age >= 20  \n"
            + "            @     break;\n"
            + "            @   case 'M':\n"
            + "                    AND n.person.age >= 22\n"
            + "            @     break;\n"
            + "            @   default:\n"
            + "                    AND n.person.age >= 24\n"
            + "            @}\n"
            + "            RETURN n\n"
            + "            LIMIT 1";
    String cql =
        render.resolve(
            text,
            new HashMap<String, Object>() {
              {
                put("gender", "M");
              }
            });
    System.out.println(cql);
  }

  @Test
  public void testSelectCase() {
    String text =
        "            MATCH (n: person)\n"
            + "            WHERE 1 == 1 \n"
            + "            @select( gender ){\n"
            + "            @   case 'F', 'M':\n"
            + "                    AND n.person.gender is not null\n"
            + "            @   default:\n"
            + "                    AND n.person.gender is null\n"
            + "            @}\n"
            + "            RETURN n\n"
            + "            LIMIT 1";
    String cql =
        render.resolve(
            text,
            new HashMap<String, Object>() {
              {
                put("gender", "M");
              }
            });
    System.out.println(cql);
  }

  @Test
  public void testDecode() {
    String text =
        "            MATCH (n: person)\n"
            + "            WHERE 1 == 1 \n"
            + "            ${ decode( gender, \n"
            + "              \"F\", \"AND n.person.age >= 20\", \n"
            + "              \"M\", \"AND n.person.age >= 22\", \n"
            + "              \"AND n.person.age >= 24\" \n"
            + "            ) }\n"
            + "            RETURN n\n"
            + "            LIMIT 1";
    String cql =
        render.resolve(
            text,
            new HashMap<String, Object>() {
              {
                put("gender", "M");
              }
            });
    System.out.println(cql);
  }

  @Test
  public void testEmptyCondition() {
    String text =
        "            MATCH (n: person)\n"
            + "            WHERE 1 == 1 \n"
            + "            ${ isEmpty( p0 ) ? '' : 'AND n.person.name == $p0' }\n"
            + "            RETURN n\n"
            + "            LIMIT 1";
    String cql =
        render.resolve(
            text,
            new HashMap<String, Object>() {
              {
                put("p0", "F");
              }
            });
    System.out.println(cql);
  }

  @Test
  public void textMapFor() {
    String text =
        "            MATCH (n: person)\n"
            + "            WHERE 1 == 1 \n"
            + "            @for ( entry in p ) {\n"
            + "              @if ( isNotEmpty( entry.value ) ) {\n"
            + "                AND n.person.`${ entry.key }` == $p.${ entry.key }\n"
            + "              @}\n"
            + "            @}\n"
            + "            RETURN n\n"
            + "            LIMIT 1";
    String cql =
        render.resolve(
            text,
            new HashMap<String, Object>() {
              {
                put(
                    "p",
                    new HashMap<String, Object>() {
                      {
                        put("name", "张三");
                        put("gender", "");
                      }
                    });
              }
            });
    System.out.println(cql);
  }

  @Test
  public void textListFor() {
    String text =
        "            @for ( p in personList ) {\n"
            + "              INSERT VERTEX `person` ( name, gender ) VALUES '${ p.name }' : ( '${"
            + " p.name }', '${ p.gender }' );\n"
            + "            @}";

    List<HashMap<String, Object>> personList =
        Arrays.asList(
            new HashMap<String, Object>() {
              {
                put("name", "张三");
                put("gender", "F");
              }
            },
            new HashMap<String, Object>() {
              {
                put("name", "王五");
                put("gender", "M");
              }
            },
            new HashMap<String, Object>() {
              {
                put("name", "赵六");
                put("gender", "F");
              }
            });
    System.out.println(JSON.toJSONString(personList));
    String cql =
        render.resolve(
            text,
            new HashMap<String, Object>() {
              {
                put("personList", personList);
              }
            });
    System.out.println(cql);
  }

  /*
   * https://github.com/nebula-contrib/ngbatis/issues/14
   */
  @Test
  public void textForBatchInsert() {
    String text =
        "  INSERT VERTEX ${tagName}\n"
            + "   (\n"
            + "      @for ( param in params ) {\n"
            + "          ${param} ${ paramLP.last ? '' : ',' }\n"
            + "      @}\n"
            + "   )\n"
            + "  VALUES\n"
            + "  @for ( datas in dataList ) {\n"
            + "      @var id = @datas.get(vidKey); \n"
            + "      ${ type.name( id ) == 'String' ? (\"'\" + id + \"'\") : id } : (\n"
            + "      @for ( param in params ) {\n"
            + "          @var col = @datas.get(param); \n"
            + "          @var colFmt = type.name( col ) == 'String' ? (\"'\" + col+ \"'\") : col;"
            + " \n"
            + "          @var colNullable = isNotEmpty( colFmt ) ? colFmt : 'null'; \n"
            + "          ${ colNullable } ${ paramLP.last ? '' : ',' }\n"
            + "      @}\n"
            + "      )  ${ datasLP.last ? '' : ',' }\n"
            + "  @}\n"
            + ";";
    System.out.println(text);
    List<HashMap<String, Object>> personList =
        Arrays.asList(
            new HashMap<String, Object>() {
              {
                put("name", "张三");
                put("gender", "F");
              }
            },
            new HashMap<String, Object>() {
              {
                put("name", "王五");
                put("gender", "M");
                put("age", 18);
              }
            },
            new HashMap<String, Object>() {
              {
                put("name", "赵六");
                put("age", 32);
              }
            });
    System.out.println(JSON.toJSONString(personList));
    String cql =
        render.resolve(
            text,
            new HashMap<String, Object>() {
              {
                put("tagName", "person");
                put("vidKey", "name");
                put("params", Arrays.asList("name", "gender", "age"));
                put("dataList", personList);
              }
            });
    System.out.println(cql);
  }

  @Test
  public void beetlFnTest() {
    String text =
        "  INSERT VERTEX ${tagName}\n"
            + "   (\n"
            + "      @for ( param in params ) {\n"
            + "          ${param} ${ paramLP.last ? '' : ',' }\n"
            + "      @}\n"
            + "   )\n"
            + "  VALUES\n"
            + "  @for ( datas in dataList ) {\n"
            + "      @var id = @datas.get(vidKey); \n"
            + "      ${ type.name( id ) == 'String' ? (\"'\" + id + \"'\") : id } : (\n"
            + "      @for ( param in params ) {\n"
            + "          @var col = @datas.get(param); \n"
            + "          ${ nvl( ng.valueFmt( col ), 'null' ) } ${ paramLP.last ? '' : ',' }\n"
            + "      @}\n"
            + "      )  ${ datasLP.last ? '' : ',' }\n"
            + "  @}\n;";
    System.out.println(text);
    List<HashMap<String, Object>> personList =
        Arrays.asList(
            new HashMap<String, Object>() {
              {
                put("name", "张三");
                put("gender", "F");
              }
            },
            new HashMap<String, Object>() {
              {
                put("name", "王五");
                put("gender", "M");
                put("age", 18);
              }
            },
            new HashMap<String, Object>() {
              {
                put("name", "赵六");
                put("age", 32);
              }
            });
    System.out.println(JSON.toJSONString(personList));
    String cql =
        render.resolve(
            text,
            new HashMap<String, Object>() {
              {
                put("tagName", "person");
                put("vidKey", "name");
                put("params", Arrays.asList("name", "gender", "age"));
                put("dataList", personList);
              }
            });
    System.out.println(cql);
  }
}