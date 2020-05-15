package com.demo.demos.FindU.Camera.filter;

import com.demo.demos.R;
import com.demo.demos.FindU.Camera.utils.GLUtil;


import static android.opengl.GLES30.*;
/**
 * Created by wangyt on 2019/5/27
 */
public class ColorFilter extends BaseFilter {

    public static final String UNIFORM_COLOR_FLAG = "colorFlag";

    public static int COLOR_FLAG = 0;

    public int hColorFlag;



    @Override
    public int initProgram() {
        return GLUtil.createAndLinkProgram(R.raw.texture_vertex_shader, R.raw.texture_color_fragtment_shader);
    }

    @Override
    public void initAttribLocations() {
        super.initAttribLocations();

        hColorFlag = glGetUniformLocation(program, UNIFORM_COLOR_FLAG);
    }

    @Override
    public void setExtend() {
        super.setExtend();
        glUniform1i(hColorFlag, COLOR_FLAG);
    }

}
