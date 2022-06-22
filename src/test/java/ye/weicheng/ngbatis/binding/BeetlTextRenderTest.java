// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.binding;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ye.weicheng.ngbatis.config.ParseCfgProps;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author yeweicheng
 * @since 2022-06-21 2:56
 * <br>Now is history!
 */
class BeetlTextRenderTest {

    private BeetlTextRender render = new BeetlTextRender();

    @BeforeEach
    public void setRenderProp() {
        render.setProps( new ParseCfgProps() );
    }

    @Test
    public void test() {
        String text = "        MATCH (n:${ tag })\n" +
                "        WHERE\n" +
                "         @if ( isNotEmpty (columns)  ) {\n" +
                "           @for ( col in columns ) {\n" +
                "               n.${ tag }.${ col } == $${ col }\n" +
                "           @}\n" +
                "        @}\n" +
                "        LIMIT 4000";
        String cql = render.resolve(
                text,
                new HashMap<String, Object>() {{
                    put( "columns", Arrays.asList( "name" ));
                    put( "tag", "person");
                }}
        );
        System.out.println( cql );
    }


    @Test
    public void testNull() {
        String text = "${ nvl( name, 'null' ) }";
        String cql = render.resolve(
                text,
                new HashMap<String, Object>() {{
                    put( "name", null);
                }}
        );
        System.out.println( cql );
    }

    @Test
    public void test2() {
        String text =
                "        MATCH (n:${ tag })\n" +
                "        @if ( isNotEmpty(columns) ) {\n" +
                "            WHERE\n" +
                "            @for ( col in columns ) {\n" +
                "                n.${ tag }.${ col } == ${ @valueColumns.get( colLP.index - 1 ) }\n" +
                "                @if (!colLP.last) {\n" +
                "                    and\n" +
                "                @}\n" +
                "            @}\n" +
                "        @}\n" +
                "        RETURN n\n" +
                "        LIMIT 4000";
        String cql = render.resolve(
                text,
                new HashMap<String, Object>() {{
                    put( "columns", Arrays.asList( "name" ));
                    put( "valueColumns", Arrays.asList( "'$name'" ));
                    put( "tag", "person");
                }}
        );
        System.out.println( cql );
    }

}