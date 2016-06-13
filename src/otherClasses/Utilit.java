package otherClasses;

import javax.swing.*;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Utilit
{
    public static double INPUT[][] = {
            { 73.25, 27.02, 48.33 },
            { 72.63, 29.17, 47.32 },
            { 74.07, 28.42, 45.44 },
            { 73.32, 29.36, 46.40 },
            { 73.16, 26.85, 46.50 },
            { 73.66, 28.17, 48.00 },
            { 74.34, 27.75, 45.67 },
            { 72.56, 23.33, 46.27 },
            { 73.26, 28.83, 44.81 },
            { 73.52, 28.97, 51.01 },
            { 73.80, 28.79, 44.07 },
            { 72.20, 30.33, 45.34 },
            { 76.73, 26.62, 52.49 },
            { 72.49, 31.02, 48.18 },
            { 76.60, 26.62, 48.11 },
            { 76.96, 26.91, 55.51 },
            { 73.30, 30.21, 41.69 },
            { 73.43, 29.17, 49.91 },
            { 74.28, 28.43, 46.15 },
            { 72.30, 30.18, 46.17 },
            { 76.66, 25.57, 51.20 },
            { 74.17, 29.11, 50.53 },
            { 74.93, 28.31, 45.64 },
            { 74.39, 28.45, 50.67 },
            { 72.89, 29.40, 44.98 }
    };

    public static double IDEAL[][] = {
            { 0.22, 0.043 },
            { 0.242, 0.043 },
            { 0.198, 0.044 },
            { 0.22, 0.048 },
            { 0.22, 0.04 },
            { 0.242, 0.048 },
            { 0.198, 0.04 },
            { 0.242, 0.04 },
            { 0.198, 0.048 },
            { 0.264, 0.043 },
            { 0.176, 0.044 },
            { 0.22, 0.053 },
            { 0.22, 0.035 },
            { 0.264, 0.053 },
            { 0.176, 0.035 },
            { 0.264, 0.035 },
            { 0.176, 0.053 },
            { 0.264, 0.048 },
            { 0.176, 0.048 },
            { 0.242, 0.053 },
            { 0.198, 0.035 },
            { 0.264, 0.04 },
            { 0.176, 0.04 },
            { 0.242, 0.035 },
            { 0.198, 0.053 }
    };

    public static double maxDifference = 0;

    public static double[][] getColorCoordValuesFromDB(String batchName)
    {
        int count = getCountBatchElements(batchName);
        double[][] array = new double[count][3];
        Statement dtsSt, colorCoordSt;
        ResultSet dtsRs, colorCoordRs;
        int i = 0;
        try
        {
            dtsSt = MySQLConnect.getConnection().createStatement();
            String dtsRecordQuery = "Select * from datatrainingset where batch =" + getIdByNameFromBatchDB(batchName);
            dtsRs = dtsSt.executeQuery(dtsRecordQuery);
            while (dtsRs.next())
            {
                Integer colCoordId = dtsRs.getInt("colorCoordinates");
                colorCoordSt = MySQLConnect.getConnection().createStatement();
                String colorCoordRecordQuery = "Select * from colorcoordinates where idColorCoordinates = " + colCoordId;
                colorCoordRs = colorCoordSt.executeQuery(colorCoordRecordQuery);
                while (colorCoordRs.next())
                {
                    array[i][0] = colorCoordRs.getDouble("lValue");
                    array[i][1] = colorCoordRs.getDouble("aValue");
                    array[i][2] = colorCoordRs.getDouble("bValue");
                }
                i++;
            }
        } catch (SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return array;
    }

    public static double[][] getDyeValuesFromDB(String batchName, int dyeCount)
    {
        int count = getCountBatchElements(batchName);
        double[][] array = new double[count][dyeCount];
        Statement dtsSt;
        ResultSet dtsRs;
        int i = 0;
        try
        {
            dtsSt = MySQLConnect.getConnection().createStatement();
            String dtsRecordQuery = "Select * from datatrainingset where batch =" + getIdByNameFromBatchDB(batchName);
            dtsRs = dtsSt.executeQuery(dtsRecordQuery);
            while (dtsRs.next())
            {
                Integer firstDyeId = dtsRs.getInt("firstDye");
                Integer secondDyeId = dtsRs.getInt("secondDye");
                Integer thirdDyeId = dtsRs.getInt("thirdDye");
                if (dyeCount >= 1) array[i][0] = Double.valueOf(getValuePerIdFromDB("firstdye", "idFirstDye", firstDyeId.toString(), "concValue").toString());
                if (dyeCount >= 2) array[i][1] = Double.valueOf(getValuePerIdFromDB("seconddye", "idSecondDye", secondDyeId.toString(), "concValue").toString());
                if (dyeCount >= 3) array[i][2] = Double.valueOf(getValuePerIdFromDB("thirddye", "idThirdDye", thirdDyeId.toString(), "concValue").toString());
                i++;
            }
        } catch (SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return array;
    }

    private static Object getValuePerIdFromDB(String table, String idName, String idValue, String nameField)
    {
        Object result = null;
        try
        {
            Statement st = MySQLConnect.getConnection().createStatement();
            String recordQuery = "Select * from " +  table + " where " + idName + " = " + idValue;
            ResultSet rs = st.executeQuery(recordQuery);
            while (rs.next())
            {
                result = rs.getObject(nameField);
            }
            return result;
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static int getCountBatchElements(String batchName)
    {
        int count = 0;
        Statement st;
        ResultSet rs;
        try
        {
            st = MySQLConnect.getConnection().createStatement();
            String recordQuery = "Select * from datatrainingset where batch = " + getIdByNameFromBatchDB(batchName);
            rs = st.executeQuery(recordQuery);
            while (rs.next())
            {
                count++;
            }
        } catch (SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return count;
    }

    public static int getIdByNameFromBatchDB(String batchName)
    {
        int id = 0;
        Statement st;
        ResultSet rs;
        try
        {
            st = MySQLConnect.getConnection().createStatement();
            String recordQuery = "Select * from batch where name = '" + batchName + "'";
            rs = st.executeQuery(recordQuery);
            while (rs.next())
            {
                id = rs.getInt("idBatch");
            }
        } catch (SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return id;
    }

    public static int getNumberOfDyes(String batchName)
    {
        int amount  = 0;
        Statement st;
        ResultSet rs;
        try
        {
            st = MySQLConnect.getConnection().createStatement();
            String recordQuery = "Select * from datatrainingset where batch = " + getIdByNameFromBatchDB(batchName);
            rs = st.executeQuery(recordQuery);
            while (rs.next())
            {
                int idFirtDyeValue = rs.getInt("firstDye");
                int idSecondDyeValue = rs.getInt("secondDye");
                int idThirdDyeValue = rs.getInt("thirdDye");
                if (checkDyeValue("firstDye", "idFirstDye", idFirtDyeValue)) amount++;
                if (checkDyeValue("secondDye", "idSecondDye", idSecondDyeValue)) amount++;
                if (checkDyeValue("thirdDye", "idThirdDye", idThirdDyeValue)) amount++;
                return amount;
            }
        } catch (SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return amount;
    }

    public static boolean checkDyeValue(String table, String idName, int idDye)
    {
        Statement st;
        ResultSet rs;
        try
        {
            st = MySQLConnect.getConnection().createStatement();
            String recordQuery = "Select * from " + table + " where " + idName + " = " + idDye;
            rs = st.executeQuery(recordQuery);
            while (rs.next())
            {
                if (rs.getDouble("concValue") > 0) return true;
                else return false;
            }
        } catch (SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return false;
    }

    public static boolean rgbRangeIsCorrently(String batchName,
                                              double lIdealValue,
                                              double aIdealValue,
                                              double bIdealValue,
                                              double allowDifferent)
    {
        double currentDifference;
        double[][] array = getColorCoordValuesFromDB(batchName);

        maxDifference = 0;
        for(int i = 0; i < array.length; i++)
        {
            for (int j = 0; j < array[i].length; j++)
            {
                currentDifference = differenceE(
                        lIdealValue,
                        aIdealValue,
                        bIdealValue,
                        array[i][0],
                        array[i][1],
                        array[i][2]);
                if (currentDifference > maxDifference) maxDifference = currentDifference;
                if (currentDifference > allowDifferent)
                {
                    return false;
                }
            }
        }
        return true;
    }

    private static double differenceE(double etalonL, double etalonA, double etalonB, double testL, double testA, double testB)
    {
        return Math.sqrt((Math.pow(etalonL - testL, 2)) + (Math.pow(etalonA - testA, 2)) + (Math.pow(etalonB - testB, 2)));
    }

    private static void writeToFile(String text)
    {
        File logFile = new File("log.txt");
        try
        {
            PrintWriter out = new PrintWriter(logFile.getAbsoluteFile());

            try
            {
                out.print(text);
            }
            finally
            {
                out.close();
            }
        } catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static String readFromFile() throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        File file = new File("log.txt");
        if (!file.exists())
        {
            throw new FileNotFoundException(file.getName());
        }
        try
        {
            BufferedReader in = new BufferedReader(new FileReader( file.getAbsoluteFile()));
            try
            {
                String s;
                while ((s = in.readLine()) != null)
                {
                    sb.append(s);
                    sb.append("\n");
                }
            }
            finally
            {
                in.close();
            }
        } catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public static void updateLog(String newText) throws FileNotFoundException
    {
        File logFile = new File("log.txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        StringBuilder sb = new StringBuilder();
        String oldFile = readFromFile();
        sb.append(oldFile);
        sb.append(newText);
        writeToFile(sb.toString());
    }

    public static boolean checkIntData(String data)
    {
        try
        {
            Integer.parseInt(data);
        } catch (NumberFormatException  ex)
        {
            return false;
        }
        return true;
    }

    public static void printArray(double[][] array)
    {
        for(double[] row : array)
        {
            for (double value : row)
            {
                System.out.print(value + "\t");
            }
            System.out.println();
        }
    }
}
