import java.io.*;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;

public class hcms2_202321811 {
    public static void main(String[] args) throws IOException {
        int count;
        String command=null;
        Highway highway = new Highway();
        EnterGate enterGate;
        ExitGate exitGate;
        CarList cl;
        Time time;

        Scanner scan = new Scanner(new File("hcms.txt"));
        exitGate = new ExitGate(scan.nextInt(), scan.nextInt());
        cl = new CarList(scan.nextInt());
        cl.setcarList(scan);
        scan.close();

        time = new Time(2024, 3, 20, 21, 0);

        Scanner in = new Scanner(System.in);
        while(true){
            try{
                command=in.next();
            } catch(Exception e){
                System.out.println("[error - wrong command character!]");
                in.nextLine();
            }

            if(command.equals("t")){
                Time temp = new Time(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());
                if(time.checkTimeError(temp)){
                    time = temp;
                }
                else continue;
                highway.updateCar(cl.getcarList(), time, exitGate);
                continue;
            }

            else if(command.equals("n")){
                int num=0;
                try{
                    num = in.nextInt();
                } catch(Exception e) { System.out.println("[error - car number format is wrong!]"); in.nextLine(); }
                count=0;
                for(Car c: cl.getcarList()){
                    if(c.getNumber()==num){
                        count++;
                        if(c.getOnway()==1||c.getOnway()==2) { System.out.println("[error - Aleardy running car!]"); in.nextLine(); continue; }
                        enterGate = new EnterGate(time);
                        try{
                            enterGate.enterCar(c, in.next(), in.next(), time);
                        } catch(Exception e) { System.out.println("[error - Point format is wrong!]"); in.nextLine(); }
                    }
                }
                if(count==0) { System.out.println("[error - car number does not exist!]"); in.nextLine(); }
                continue;
            }

            else if(command.equals("o")){
                count=0;
                for(Car c: cl.getcarList()){
                    if(c.getOnway()==1||c.getOnway()==2){
                        count++;
                        System.out.println(count+". "+c.getNumber()+" "+c.getPosition()+"km");
                    }
                }
                if(count==0) System.out.println("통행 차량이 없습니다!");
                continue;
            }

            else if(command.equals("x")){
                count=0;
                for(Car c: cl.getcarList()){
                    if(c.getOnway()==3){
                        count++;
                        System.out.println(count+". "+c.getNumber()+" "+c.getExitTime()+" "+c.getFee()+"원");
                    }
                }
                if(count==0) System.out.println("진출한 차량이 없습니다!");
                continue;
            }

            else if(command.equals("r")){
                try{
                    cl.registerCar(in.nextInt(), in.nextDouble(), in.nextDouble());
                } catch(Exception e){
                    System.out.println("[error - car information format is wrong!]");
                    in.nextLine();
                }
                
                continue;
            }

            else if(command.equals("q")){
                in.close();
                System.exit(0);
            }

            else{
                System.out.println("[error - wrong command character!]");
                in.nextLine();
                continue;
            }
        }

    }
}

class CarList {
    private int current;
    private Car[] carList;

    CarList(int n){
        current=n;
        carList = new Car[n];
    }

    public Car[] getcarList(){ return carList; }
    public void setcarList(Scanner s){
        for(int i=0;i<current;i++){
            carList[i] = new Car(s.nextInt(), s.nextDouble(), s.nextDouble());
        }
    }
    public void registerCar(int n,double d,double s){
        for(Car c: carList){
            if(c.getNumber()==n) {
                System.out.println("[error - already registered car number!]");
                return;
            }
        }
        current++;
        Car[] temp = new Car[current];
        for(int i=0;i<current-1;i++){
            temp[i]=carList[i];
        }
        temp[current-1]=new Car(n,d,s);
        carList=temp;
    }
}

class Car {
    private final int number;
    private final double displacement;
    private final double speed;
    private int onway=0;
    private Time enterTime;
    private String enterPoint;
    private int enterDistance;
    private Time exitTime;
    private String exitPoint;
    private int exitDistance;
    private int distance;
    private int position;
    private int fee;

    Car(int number, double displacement, double speed){
        this.number=number; this.displacement=displacement; this.speed=speed;
    }

    public int getNumber(){ return number; }
    public double getDisplacement(){ return displacement; }
    public double getSpeed(){ return speed; }
    public void setOnway(int onway){
        this.onway = onway;
    }
    public int getOnway(){ return onway; }
    public void setEnterTime(Time t){
        enterTime=t;
    }
    public Time getEnterTime() { return enterTime; }
    public int getEnterDistatnce() { return enterDistance; }
    public void setExitTime(Time t){
        exitTime=t;
    }
    public Time getExitTime() { return exitTime; }
    public int getExitDistance() { return exitDistance; }
    public void setPoint(int enterDistance, int exitDistance) {
        this.enterDistance=enterDistance; this.exitDistance=exitDistance;
    }
    public void setDistance(int distance){
        this.distance=distance;
    }
    public int getDistance(){ return distance; }
    public void setPosition(int position){
        this.position=position;
    }
    public int getPosition(){ return position; }
    public void setFee(int fee){
        this.fee=fee;
    }
    public int getFee(){ return fee; }
}

class EnterGate {
    private Time enterTime;

    EnterGate(Time t){
        enterTime=t;
    }

    public Time getEnterTime(){ return enterTime; }
    public boolean checkPoint(String p1, String p2){
        if(p1.equals(p2)) { System.out.println("[error - entry point and exit point must be different!]"); return false; }
        if((p1.equals("서울")||p1.equals("수원")||p1.equals("대전")||p1.equals("대구")||p1.equals("부산"))==false) { System.out.println("[error - entry point should be included in list!]"); return false; }
        if((p2.equals("서울")||p2.equals("수원")||p2.equals("대전")||p2.equals("대구")||p2.equals("부산"))==false) { System.out.println("[error - exit point should be included in list!]"); return false; }
        return true;
    }
    public void enterCar(Car c,String p1,String p2,Time t){
        if(checkPoint(p1,p2)){
            int d1, d2;
            switch(p1){
                case "서울": d1=0; break;
                case "수원": d1=30; break;
                case "대전": d1=130; break;
                case "대구": d1=290; break;
                default: d1=400; break;
            }
            switch(p2){
                case "서울": d2=0; break;
                case "수원": d2=30; break;
                case "대전": d2=130; break;
                case "대구": d2=290; break;
                default: d2=400; break;
            }
            if(d2-d1>0){
                c.setDistance(d2-d1);
                c.setOnway(1);
            }
            else{
                c.setDistance(d1-d2);
                c.setOnway(2);
            }
            c.setPoint(d1,d2);
            c.setPosition(d1);
            c.setEnterTime(enterTime);
            c.setExitTime(t.plusTime((int)(c.getDistance()/c.getSpeed()*60)));            
        }
    }

}

class ExitGate {
    private static int basicFee;
    private static int distanceRate;

    ExitGate(int basicFee, int distanceRate) {
        this.basicFee=basicFee; this.distanceRate=distanceRate;
    }

    public int calcFee(Car c){
        return (int)((basicFee+c.getDistance()*distanceRate)*(c.getSpeed()/100)*(c.getDisplacement()/2000)/10)*10;
    }
    public void exitCar(Car c){
        c.setOnway(3);
        c.setFee(calcFee(c));
    }
}

class Highway {

    public void updateCar(Car[] cl, Time t, ExitGate e){
        for(Car c: cl){
            if(c.getOnway()==1||c.getOnway()==2){
                int migration = (int)(t.getInterval(c.getEnterTime(), t)*c.getSpeed()/60.0);
                if(migration<c.getDistance()){
                    if(c.getOnway()==1){
                        c.setPosition(c.getEnterDistatnce()+migration);
                    }
                    else{
                        c.setPosition(c.getEnterDistatnce()-migration);
                    }
                }
                else{
                    e.exitCar(c);
                }
            }
        }
    }
}

class Time {
    private int year;
    private int month;
    private int date;
    private int hour;
    private int minute;

    Time(int year,int month,int date,int hour,int minute){
        this.year=year; this.month=month; this.date=date; this.hour=hour; this.minute=minute;
    }

    public int getYear(){ return year; }
    public int getMonth(){ return month; }
    public int getDate(){ return date; }
    public int getHour(){ return hour; }
    public int getMinute(){ return minute; }

    public boolean checkTimeError(Time t1){
        switch(t1.month){
            case 1,3,5,7,8,10,12:
            if(1>t1.date||t1.date>31) { System.out.println("[error - time is not valid!]"); return false; }
            else break;
            case 4,6,9,11:
            if(1>t1.date||t1.date>30) { System.out.println("[error - time is not valid!]"); return false; }
            else break;
            case 2:
            if(t1.year%4==0&&t1.year%100!=0){
                if(1>t1.date||t1.date>29) { System.out.println("[error - time is not valid!]"); return false; }
                else break;
            }
            else {
                if(1>t1.date||t1.date>30) { System.out.println("[error - time is not valid!]"); return false; } 
                else break;
            }
            default: { System.out.println("[error - time is not valid!]"); return false; }
        }
        if(t1.hour>23||t1.hour<0||t1.minute>59||t1.minute<0) { System.out.println("[error - time is not valid!]"); return false; }
        if(getInterval(this, t1)<=0) { System.out.println("[error - time should be later than current!]"); return false; }
        return true;        
    }
    public long getInterval(Time t2, Time t3){
        LocalDateTime future = LocalDateTime.of(t2.getYear(),t2.getMonth(),t2.getDate(),t2.getHour(),t2.getMinute());
        LocalDateTime present = LocalDateTime.of(t3.getYear(),t3.getMonth(),t3.getDate(),t3.getHour(),t3.getMinute());
        Duration duration = Duration.between(future, present);
        long durMinute = duration.toMinutes();
        return durMinute;
    }
    public Time plusTime(int plus){
        LocalDateTime present = LocalDateTime.of(this.year, this.month, this.date, this.hour, this.minute);
        LocalDateTime future = present.plusMinutes(plus);
        Time newTime = new Time(future.getYear(),future.getMonthValue(),future.getDayOfMonth(),future.getHour(),future.getMinute());
        return newTime;
    }
    public String toString(){
        return String.format("%02d/%02d/%02d-%02d:%02d", getYear(), getMonth(), getDate(), getHour(), getMinute());
    }
        
}
