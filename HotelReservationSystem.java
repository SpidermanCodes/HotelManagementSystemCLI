import java.sql.*;
import java.util.Scanner;

public class HotelReservationSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private  static final String username = "root";
    private static final String password = "0000";
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            while (true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1.  Reserve a room");
                System.out.println("2.  View reservation");
                System.out.println("3.  Get room number");
                System.out.println("4.  Update reservation");
                System.out.println("5.  Delete reservation");
                System.out.println("0.  exit");
                System.out.println("Choose an option");
                int choice = scanner.nextInt();

                switch (choice){
                    case 1:
                        reserveRoom(connection, scanner, connection.createStatement());
                        break;
                    case 2:
                        viewReservations(connection, scanner);
                        break;
                    case 3:
                        getRoomNumber(connection, scanner);
                        break;
                    case 4:
                        updateReservation(connection, scanner);
                        break;
                    case  5:
                        deleteReservation(connection, scanner);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("invalid choice");
                }
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }


    public  static  void reserveRoom(Connection connection, Scanner scanner, Statement statement){
        try {
            System.out.println("Enter guest name: ");
            String guestName = scanner.next();
            scanner.nextLine();
            System.out.println("Enter room number: ");
            int roomNumber = scanner.nextInt();
            System.out.println("Enter contact number");
            String contactNumber = scanner.next();

            String sql  = "INSERT INTO  reservations (guest_name, room_number, contact_number) " +
                    "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";

            //try (Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);
                if(affectedRows > 0){
                    System.out.println("Reservation successfull");
                }else{
                    System.out.println("reservation failed");
                }
           // }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }


    public static void viewReservations(Connection connection, Scanner scanner) throws SQLException{
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";
       try (Statement statement = connection.createStatement();
           ResultSet resultSet = statement.executeQuery(sql)) {

               System.out.println("current reservation: ");
               System.out.println("+-----------------+-----------------+-----------------+-----------------+-----------------");
               System.out.println("| Reservation_id  |      Guest      |    Room number  |  Contact number | reservation date |" );
               System.out.println("+-----------------+-----------------+-----------------+-----------------+-----------------");

               while (resultSet.next()){
                   int reservationId = resultSet.getInt("reservation_id");
                   String guestName = resultSet.getString("guest_name");
                   int roomNumber = resultSet.getInt("room_number");
                   String contactNumber = resultSet.getString("contact_number");
                   String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                   System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s |\n",
                           reservationId, guestName, roomNumber, contactNumber, reservationDate);

               }
                System.out.println("+-----------------+-----------------+-----------------+-----------------+-----------------");

           }

    }


    public static void getRoomNumber(Connection connection, Scanner scanner){
        try{
            System.out.print("Enter reservation id: ");
            int reservationId = scanner.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = scanner.next();

            String sql = "SELECT room_number FROM reservations " +
                    "WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";

            try(Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)){

                if(resultSet.next()){
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for reservation id " + reservationId +
                            " and guest " + guestName + " is " + roomNumber);
                }
                else{
                    System.out.println("reservation not found for the given id and guest name");
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }


    public static void updateReservation(Connection connection, Scanner scanner){
        try {
            System.out.println("Enter reservation id to upgrade: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine();

            if(!reservationExists(connection, reservationId)){
                System.out.println("Reservation not found for the given id");
                return;
            }

            System.out.println("Enter new guest name: ");
            String newGuestName = scanner.nextLine();
            System.out.println("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            System.out.println("Enter new contact number: ");
            String newContactNumber = scanner.next();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "',  " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationId;

            try(Statement statement = connection.createStatement()) {
                int affectedRow = statement.executeUpdate(sql);
                if (affectedRow > 0) {
                    System.out.println("Reservation updated successfull");
                } else {
                    System.out.println("reservation updated failed ");
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }


    public  static  void deleteReservation(Connection connection, Scanner scanner){
        try {
            System.out.println("Enter reservation id to delete: ");
            int reservationId = scanner.nextInt();

            if(!reservationExists(connection, reservationId)){
                System.out.println("Reservation not found for the given ID");
                return;
            }
            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try(Statement statement = connection.createStatement()){
                int affectedRow = statement.executeUpdate(sql);

                if(affectedRow > 0){
                    System.out.println("reservation deleted sucessfully");
                }
                else{
                    System.out.println("reservation deletion failed");
                }

            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }


    public static boolean reservationExists(Connection connection, int reservationId){
        try{
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;
            try(Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)){
                return resultSet.next(); // any result means reservation exists
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }


    public static  void exit() throws InterruptedException{
        System.out.print("Existing system");
        int i = 5;
        while (i!=0){
            System.out.print(".");
            Thread.sleep(800);
            i--;
        }
        System.out.println();
        System.out.println("Thank you for using hotel reservation");
    }
}
