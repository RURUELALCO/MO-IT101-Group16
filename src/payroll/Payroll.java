package payroll;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Payroll {
    public static double employee_hourly_rate;
    public static double totalWorkingHoursss;
    public static double monthlyBasicSalary;
    public static double totalmonthlyBasicSalary;
    public static double totalLateMinutes;
    public static double latededuct;
    // connection to "EmployeeDetails.csv" "AttendanceDetails.csv" "SSSContribution.csv"
    //ask user to input name or emplooyee no.
    //log out 
 public static void main(String[] args) {
    String employeeCsvFile = "EmployeeDetails.csv";
    String attendanceCsvFile = "AttendanceDetails.csv";
    String SSSContributioncsvFile = "SSSContribution.csv";
    

    while (true) {
        boolean found = false;

        try {
            Scanner scanner = new Scanner(System.in);
            String[] employeeData = null;

            BufferedReader attendanceReader = new BufferedReader(new FileReader(attendanceCsvFile));

            while (!found) {
                BufferedReader employeeReader = new BufferedReader(new FileReader(employeeCsvFile));
                
              
                System.out.print("Enter employee number or full name (Last Name First Name): ");
                String input = scanner.nextLine().trim();
                String line;
                while ((line = employeeReader.readLine()) != null) {
                    String[] data = parseCSVLine(line);
                    if (data[0].equalsIgnoreCase(input) || (data[1] + " " + data[2]).equalsIgnoreCase(input)) {
                        employeeData = data;
                        found = true;
                        break;
                    }
                }

                employeeReader.close();

                if (!found) {
                    System.out.println("No matching data found for input: " + input);
                    System.out.println("Please try again.");
                }
            }

            while (true) {
                String employeeFullName = employeeData[1] + " " + employeeData[2];
                System.out.print("******************************************");
                System.out.println("\nEmployee Name: " + employeeFullName + ":");
                System.out.println("Employee Number: " + employeeData[0] + ":");
                
                employee_hourly_rate = Double.parseDouble(employeeData[18]); // Convert string to double
                System.out.println("******************************************");
                System.out.println("\nMenu:");
                System.out.println("1. Employee Information");
                System.out.println("2. Employee salary information per month");
                System.out.println("3. Employee salary information per week");
                System.out.println("4. Log out");
                System.out.print("Select an option: ");

                
                if (scanner.hasNextInt()) {
                    int choice = scanner.nextInt();
                    scanner.nextLine(); 

                    if (choice < 1 || choice > 4) {
                        System.out.println("Selection not valid. Please select again.");
                        continue;
                    }

                    switch (choice) {
                        case 1:
                            printEmployeeDetails(employeeData);
                             break;
                        case 2:
                            viewSalaryInformation(employeeData[0], attendanceCsvFile);
                            break;
                            
                        case 3:
                        viewCustom7DaysAttendance(employeeData[0], attendanceCsvFile);
                            break;    
                            
                        case 4:
                            System.out.println("Logging out...");
                            break; 
                        default:
                            System.out.println("Invalid option.");
                    }

                    if (choice == 4) {
                        break; // Exit the inner loop when logging out
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number between 1 and 3.");
                    scanner.nextLine(); // Consume invalid input
                }
            }

            // Prompt for new employee number or full name after logging out
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
private static void printEmployeeDetails(String[] data) {
        System.out.println("******************************************");
        System.out.println("Employee Details:"); 
        
        System.out.printf("%-20s%-15s%-30s%-30s%-20s%n", "Birthdate:", "Status:", "Position:", "Immediate Supervisor:", "Contact No.:");
        System.out.printf("%-20s%-15s%-30s%-30s%-20s%n", data[3], data[10], data[11], data[12], data[5]);
        System.out.printf("%-20s %s%n", "Employee Address", ":");
        System.out.printf("%-20s %s%n", data[4], "");
        System.out.println("******************************************");
        System.out.printf("%-20s%-20s%-30s%-20s%n", "Pholhealth No.:", "Tin No.:", "SSS No.:", "Pag-ibig No.:");
        System.out.printf("%-20s%-20s%-30s%-20s%n", data[7], data[8], data[6], data[9]);
        //employee_hourly_rate = data[18];
    }
private static String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean withinQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == ',' && !withinQuotes) {
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else if (c == '"') {
                withinQuotes = !withinQuotes;
            } else {
                currentField.append(c);
            }
        }

        fields.add(currentField.toString());
        return fields.toArray(new String[0]);
    }




 private static void viewSalaryInformation(String employeeNumber, String attendanceCsvFile) throws IOException {
     totalLateMinutes = 0;
        BufferedReader attendanceReader = new BufferedReader(new FileReader(attendanceCsvFile));
        attendanceReader.readLine(); // Skip header row
        List<LocalDate> startMonths = new ArrayList<>();

        while (true) {
            String line = attendanceReader.readLine();
            if (line == null) break; // Break if end of file is reached
            String[] attendanceData = line.split(",");
            LocalDate date = LocalDate.parse(attendanceData[1], DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            if (attendanceData[0].equals(employeeNumber)) {
                if (!startMonths.contains(date.withDayOfMonth(1))) {
                    startMonths.add(date.withDayOfMonth(1));
                }
            }
        }

        attendanceReader.close();

        Scanner scanner = new Scanner(System.in);
        System.out.println("******************************************");
        System.out.println("Monthly selection of your attendance:");
        for (int i = 0; i < startMonths.size(); i++) {
            LocalDate startMonth = startMonths.get(i);
            LocalDate endMonth = startMonth.plusMonths(1).minusDays(1); // Last day of the month
            System.out.println((i + 1) + ". " + startMonth.getMonth() + " " + startMonth.getYear());
            
        }

        int selectedMonth;
        while (true) {
            System.out.println("Select a month you wish to see your salary information:");
            if (scanner.hasNextInt()) {
                selectedMonth = scanner.nextInt();
                scanner.nextLine(); // Consume newline character after nextInt()
                

                if (selectedMonth < 1 || selectedMonth > startMonths.size()) {
                    System.out.println("Selection not valid. Please select again.");
                    continue;
                }
            } else {
                System.out.println("Invalid input. Please enter a number between 1 and " + startMonths.size() + ".");
                scanner.nextLine(); // Consume invalid input
                continue;
            }

            break;
        }

        LocalDate startMonth = startMonths.get(selectedMonth - 1);
        LocalDate endMonth = startMonth.plusMonths(1).minusDays(1); // Last day of the month

        System.out.println("\nTotal attendance and time spent for " + startMonth.getMonth() + " " + startMonth.getYear() + " by Employee No. " + employeeNumber + "");

        BufferedReader attendanceReader2 = new BufferedReader(new FileReader(attendanceCsvFile)); // Reopen reader to read from the start
        attendanceReader2.readLine(); // Skip header row

        String line;
        double totalWorkingHours = 0;
        while ((line = attendanceReader2.readLine()) != null) {
            String[] attendanceData = line.split(",");
            LocalDate date = LocalDate.parse(attendanceData[1], DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            if (attendanceData[0].equals(employeeNumber) && isWithinMonth(date, startMonth)) {
                System.out.println("Date: " + attendanceData[1] + ", Time: " + attendanceData[2] + " - " + attendanceData[3]);
                totalWorkingHours += calculateWorkingHours(attendanceData[2], attendanceData[3]);
                totalWorkingHoursss = totalWorkingHours;
                totalLateMinutes += calculateMinutesLate(attendanceData[2]);
                
            }
        }

        attendanceReader2.close();
        String formattedTime = convertToHoursAndMinutes(totalWorkingHours);
        System.out.println("\nTotal working hours for the selected month: " + formattedTime);
        System.out.println("Total late for the selected months: " + totalLateMinutes + " minutes"); 
        monthlyBasicSalary = employee_hourly_rate * totalWorkingHoursss;
        //need this to edit 
        //System.out.println(monthlyBasicSalary);
        printSalaryInformation(employee_hourly_rate );
        
    }
 
 private static double viewCustom7DaysAttendance(String employeeNumber, String attendanceCsvFile) throws IOException {
     totalLateMinutes = 0;
    BufferedReader attendanceReader = new BufferedReader(new FileReader(attendanceCsvFile));

    attendanceReader.readLine(); // Skip header row

    Scanner scanner = new Scanner(System.in);
    LocalDate startDate = null;
    boolean validDate = false;

    while (!validDate) {
        try {
            System.out.println("Enter start date (MM/dd/yyyy): ");
            String startDateString = scanner.nextLine().trim();
            // Check if slashes are missing and add them
            if (!startDateString.contains("/") && !startDateString.contains("-")) {
                if (startDateString.length() == 8) {
                    startDateString = startDateString.substring(0, 2) + "/" + startDateString.substring(2, 4) + "/" + startDateString.substring(4);
                } else {
                    throw new DateTimeParseException("Invalid date format", startDateString, 0);
                }
            }
            startDate = LocalDate.parse(startDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            validDate = true;
        } catch (DateTimeParseException e) {
            System.out.println("Incorrect date format. Please enter the date in MM/dd/yyyy format.");
        }
    }

    // Calculate end date which is 7 days after the start date
    LocalDate endDate = startDate.plusDays(6); // 6 days to account for the first day

    System.out.println("\nAttendance for Employee Number " + employeeNumber + " from " + startDate + " to " + endDate + ":");

    String line;
    double totalWorkingHours = 0;
    while ((line = attendanceReader.readLine()) != null) {
        String[] attendanceData = line.split(",");
        LocalDate date = LocalDate.parse(attendanceData[1], DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        if (attendanceData[0].equals(employeeNumber) && !date.isBefore(startDate) && !date.isAfter(endDate)) {
            System.out.println("Date: " + attendanceData[1] + ", Time: " + attendanceData[2] + " - " + attendanceData[3]);
            totalWorkingHours += calculateWorkingHours(attendanceData[2], attendanceData[3]);
            totalWorkingHoursss = Math.round((totalWorkingHours)* 10.0)/10.0;
             totalLateMinutes += calculateMinutesLate(attendanceData[2]);
            
            
        }
    }

    attendanceReader.close();
    String formattedTime = convertToHoursAndMinutes(totalWorkingHours);
    System.out.println("\nTotal working hours for the selected 7 days: " + formattedTime );
    System.out.println("Total late for the selected week: " +  totalLateMinutes + " minutes"); 
    monthlyBasicSalary = employee_hourly_rate * totalWorkingHoursss;
    printSalaryInformation(employee_hourly_rate );
    return totalWorkingHours;
}
 
 
private static double calculateWorkingHours(String timeIn, String timeOut) {
    if (timeIn.equals("0:00") && timeOut.equals("0:00")) {
        return 0.0;
    }
    
    String[] timeInParts = timeIn.split(":");
    String[] timeOutParts = timeOut.split(":");
    int hoursIn = Integer.parseInt(timeInParts[0]);
    int minutesIn = Integer.parseInt(timeInParts[1]);
    int hoursOut = Integer.parseInt(timeOutParts[0]);
    int minutesOut = Integer.parseInt(timeOutParts[1]);
    int totalMinutes = (hoursOut * 60 + minutesOut) - (hoursIn * 60 + minutesIn);  

    int minutesLate = calculateMinutesLate(timeIn);
    if ((hoursIn > 8) || (hoursIn == 8 && minutesIn >= 11)) {
        latededuct = minutesLate;
        System.out.println("Late for " + convertMinutesToHours(minutesLate));
    } else if ((hoursIn < 8) || (hoursIn == 8 && minutesIn <= 10)) {
        totalMinutes += minutesLate; // Add late minutes to total working hours
        latededuct = minutesLate;
        if (!(hoursIn == 8 && minutesIn == 0)) { // Check if time in is exactly 8:00
            System.out.println("Late for " + convertMinutesToHours(minutesLate));
        }
    }
    
    return totalMinutes / 60.0;
}


private static int calculateMinutesLate(String timeIn) {
    if (timeIn.equals("0:00")) {
        return 0;
    }
    
    String[] timeInParts = timeIn.split(":");
    int hoursIn = Integer.parseInt(timeInParts[0]);
    int minutesIn = Integer.parseInt(timeInParts[1]);

    // Calculate the number of minutes late if time in is after 8:00
    int expectedHours = 8; // Expected arrival hour (8:00)
    int expectedMinutes = 0; // Expected arrival minute (8:00)
    int expectedTotalMinutes = expectedHours * 60 + expectedMinutes;
    int actualTotalMinutes = hoursIn * 60 + minutesIn;
    int minutesLate = Math.max(actualTotalMinutes - expectedTotalMinutes, 0); // Prevent negative values

    return minutesLate;
}

private static String convertMinutesToHours(int minutes) {
    if (minutes < 0) {
        return "Invalid input";
    }

    int hours = minutes / 60;
    int remainingMinutes = minutes % 60;

    if (hours > 0 && remainingMinutes > 0) {
        return hours + " hours and " + remainingMinutes + " minutes";
    } else if (hours > 0) {
        return hours + " hours";
    } else {
        return minutes + " minutes";
    }
}


 
 
    private static String convertToHoursAndMinutes(double totalHours) {
    // Extract the integer part as hours
    int hours = (int) totalHours;

    // Calculate remaining minutes
    double decimalPart = totalHours - hours;
    int minutes = (int) (decimalPart * 60)  ;

    // Format the result
    return hours + " hours and " + minutes + " minutes";
    
    
}
 
 
    private static boolean isWithinMonth(LocalDate date, LocalDate month) {
        return date.getMonth() == month.getMonth() && date.getYear() == month.getYear();
    }
    

    public static double calculatePagIbigContribution(double monthlyBasicSalary) {
        double contributionRate;
        double maxContribution = 100; // Maximum contribution set to 100
        // Determine contribution rate based on monthly basic salary
        if (monthlyBasicSalary >= 1000 && monthlyBasicSalary <= 1500) {
            contributionRate = 0.01; // 1%
        } else if (monthlyBasicSalary > 1500) {
            contributionRate = 0.02; // 2%
        } else {
            contributionRate = 0; // No contribution if salary is below 1000
        }
        // Calculate contribution amount
        double contributionAmount = monthlyBasicSalary * contributionRate;
        // Check if contribution amount exceeds the maximum contribution
        if (contributionAmount > maxContribution) {
            contributionAmount = maxContribution;
        }
        return contributionAmount;
    }
   
    public static double calculatePhilhealthContribution(double monthlyBasicSalary) {
        double contributionRate;
        double maxContribution = 1800; 
        // Determine contribution rate based on monthly basic salary
        if (monthlyBasicSalary >= 10000.1 && monthlyBasicSalary <= 59999.9) {
            contributionRate = 0.03; 
        } else if (monthlyBasicSalary > 60000) {
            contributionRate = 0.03; 
        } else {
            contributionRate = 0; 
        }
        // Calculate contribution amount
        double contributionAmount = (monthlyBasicSalary * contributionRate);
        double employeeShare = contributionAmount * .50;
        double totalcontributionAmount = Math.round((contributionAmount - employeeShare)* 100.00)/100.00;
        // Check if contribution amount exceeds the maximum contribution
        if (totalcontributionAmount > maxContribution) {
            totalcontributionAmount = maxContribution;
        }
        return totalcontributionAmount;
    }
    public static double calculateSSSContribution(double monthlyBasicSalary, String SSSContributioncsvFile) {
    try (BufferedReader br = new BufferedReader(new FileReader(SSSContributioncsvFile))) {
        String line;
        br.readLine(); // Skip the header line
        while ((line = br.readLine()) != null) {
            String[] parts = parseCSVLine(line);
            double lowerLimit = parseDoubleWithComma(parts[0]);
            double upperLimit = parseDoubleWithComma(parts[2]);
            double contribution = parseDoubleWithComma(parts[3]);

            // Check if the monthly basic salary falls within the current range
            if (monthlyBasicSalary >= lowerLimit && monthlyBasicSalary <= upperLimit) {
                return contribution;
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    // If the monthly basic salary is over 25000, return a fixed contribution of 1125
    if (monthlyBasicSalary > 25000) {
        return 1125;
    }
    // If no matching range is found and salary is not over 25000, return 0 as default contribution
    return 0;
}
     //for replacing coma to space
    private static double parseDoubleWithComma(String s) {
        return Double.parseDouble(s.replace(",", ""));
    }
      //if total monthlybasic salary is equal to 20832 withholdingtax is 0
      // if total monthlybasic salary is equal to 20832 to 33333 withholdingtax is 20% times total monthly basic salary - 20833
      // if total monthlybasic salary is equal to 33333 to 66667 withholdingtax is 2500 plus 25% times total monthly basic salary - 33333
      // if total monthlybasic salary is equal to 66667 to 166667 withholdingtax is 10833 plus 30% times total monthly basic salary - 66667
      // if total monthlybasic salary is equal to 166667 to 666667 withholdingtax is 40833.33 plus 32% times total monthly basic salary - 166667
      // if total monthlybasic salary is equal higher than 666667 withholdingtax is 200833.33 plus 35% times total monthly basic salary - 666667
    public static double calculateWitholdingTax(double taxrate) {
         if (totalmonthlyBasicSalary < 20832) {
        return 0;
    } else if (totalmonthlyBasicSalary >= 20833 && totalmonthlyBasicSalary < 33333) {
        return 0.20 * (totalmonthlyBasicSalary - 20833);
    } else if (totalmonthlyBasicSalary >= 33333 && totalmonthlyBasicSalary < 66667) {
        return 2500 + 0.25 * (totalmonthlyBasicSalary - 33333);
    } else if (totalmonthlyBasicSalary >= 66667 && totalmonthlyBasicSalary < 166667) {
        return 10833 + 0.30 * (totalmonthlyBasicSalary - 66667);
    } else if (totalmonthlyBasicSalary >= 166667 && totalmonthlyBasicSalary < 666667) {
        return 40833.33 + 0.32 * (totalmonthlyBasicSalary - 166667);
    } else {
        return 200833.33 + 0.35 * (totalmonthlyBasicSalary - 666667);
    }
    }
    
    
    
    
    // printing and computing salary information 
    private static void printSalaryInformation(double employee_hourly_rate) {
    System.out.println("******************************************");
    double philhealthContribution = calculatePhilhealthContribution(monthlyBasicSalary);
    double pagIbigContribution = calculatePagIbigContribution(monthlyBasicSalary);
    double SSSContribution = calculateSSSContribution(monthlyBasicSalary, "SSSContribution.csv");
    
    latededuct = 0;
     if (totalLateMinutes <= 10) {
        
    } else {
        latededuct = totalLateMinutes * (employee_hourly_rate / 60.0);
    }
    

    double totaldeduction = philhealthContribution + pagIbigContribution + SSSContribution + latededuct;
    totalmonthlyBasicSalary = monthlyBasicSalary - totaldeduction;
    double taxRate = Math.round((calculateWitholdingTax(totalmonthlyBasicSalary))* 100.00)/100.00;
    double totalPay = Math.round((totalmonthlyBasicSalary - taxRate)* 100.00)/100.00 ;
    
    System.out.println("Total monthly base salary: " + monthlyBasicSalary);
    System.out.println("PhilHealth deductions: " + philhealthContribution );
    System.out.println("Pag-ibig contributions: " + pagIbigContribution);
    System.out.println("SSS contributions: " + SSSContribution);
    System.out.println("Total late deduction: " + latededuct);
    System.out.println("Total deductions: " + totaldeduction);
    System.out.println("TAXABLE INCOME (Salary - Total Deductions): " + totalmonthlyBasicSalary);
    System.out.println("Tax Rate: " + taxRate);
    System.out.println("Net Pay: " + totalPay);
    
    
    
} 
}