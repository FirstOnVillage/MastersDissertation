package otherClasses;

import javax.swing.*;

public class Converter
{

    public static int colR;
    public static int colG;
    public static int colB;

    public static boolean revertLABToRGB(double colorL, double colora, double colorb)
    {
        try
        {
            double var_Y =  (colorL + 16) / 116;
            double var_X = colora / 500 + var_Y;
            double var_Z = var_Y - colorb / 200;

            if (Math.pow(var_Y, 3) > 0.008856)
            {
                var_Y = Math.pow(var_Y, 3);
            } else
            {
                var_Y = (var_Y - 16 / 116) / 7.787;
            }
            if (Math.pow(var_X, 3) > 0.008856)
            {
                var_X = Math.pow(var_X, 3);
            } else
            {
                var_X = (var_X - 16 / 116) / 7.787;
            }
            if (Math.pow(var_Z, 3) > 0.008856)
            {
                var_Z = Math.pow(var_Z, 3);
            } else
            {
                var_Z = (var_Z - 16 / 116) / 7.787;
            }
            double ref_X = 95.047;
            double ref_Y = 100.000;
            double ref_Z = 108.883;
            double X = ref_X * var_X;        //  Observer= 2°, Illuminant= D65
            double Y = ref_Y * var_Y;
            double Z = ref_Z * var_Z;

            var_X = X / 100;      //X from 0 to  95.047      (Observer = 2°, Illuminant = D65)
            var_Y = Y / 100;      //Y from 0 to 100.000
            var_Z = Z / 100;       //Z from 0 to 108.883

            double var_R = var_X * 3.2406 + var_Y * -1.5372 + var_Z * -0.4986;
            double var_G = var_X * -0.9689 + var_Y * 1.8758 + var_Z * 0.0415;
            double var_B = var_X * 0.0557 + var_Y * -0.2040 + var_Z * 1.0570;

            if (var_R > 0.0031308)
            {
                var_R = 1.055 * Math.pow(var_R, (1 / 2.4)) - 0.055;
            } else
            {
                var_R = 12.92 * var_R;
            }
            if (var_G > 0.0031308)
            {
                var_G = 1.055 * Math.pow(var_G, (1 / 2.4)) - 0.055;
            } else
            {
                var_G = 12.92 * var_G;
            }
            if (var_B > 0.0031308)
            {
                var_B = 1.055 * Math.pow(var_B, (1 / 2.4)) - 0.055;
            } else
            {
                var_B = 12.92 * var_B;
            }

            double R = var_R * 255;
            double G = var_G * 255;
            double B = var_B * 255;
            colR = (int) R;
            colG = (int) G;
            colB = (int) B;
            if (colR < 0)
            {
                colR = 0;
            }
            if (colR > 255)
            {
                colR = 255;
            }
            if (colG < 0)
            {
                colG = 0;
            }
            if (colG > 255)
            {
                colG = 255;
            }
            if (colB < 0)
            {
                colB = 0;
            }
            if (colB > 255)
            {
                colB = 255;
            }
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, e);
            return false;
        }
        return true;
    }
}
