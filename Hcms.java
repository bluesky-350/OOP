import java.time.LocalDateTime;
import java.time.Duration;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class hcms3_202321811 { // 23 lines!!!
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("hcms.txt"));
        ExitGate.setExitGate(br);
        VehicleList.setVehicleList(br);

        CurrentTime.setCurrentTime(new Time(2024, 3, 20, 21, 00));

        br = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            StringTokenizer st = new StringTokenizer(br.readLine());
            switch(st.nextToken()){
                case "t": OrderT o1 = new OrderT(); o1.execute(st, CurrentTime.getCurrentTime()); break;
                case "n": OrderN o2 = new OrderN(); o2.execute(st, CurrentTime.getCurrentTime()); break;
                case "o": OrderO o3 = new OrderO(); o3.execute(st, CurrentTime.getCurrentTime()); break;
                case "x": OrderX o4 = new OrderX(); o4.execute(st, CurrentTime.getCurrentTime()); break;
                case "r": OrderR o5 = new OrderR(); o5.execute(st, CurrentTime.getCurrentTime()); break;
                case "q": br.close(); System.exit(0);
                default: OrderError o7 = new OrderError(); o7.execute(st, CurrentTime.getCurrentTime()); break;
            }
        }
    }
}

interface Command {
    public abstract void execute(StringTokenizer st, Time time);
}

class OrderT implements Command {
    @Override
    public void execute(StringTokenizer st, Time time){
        Time temp = new Time(Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()));
        if(time.checkTimeError(temp)){
            CurrentTime.setCurrentTime(temp);
            for(Vehicle v: VehicleList.getVehicleList()){
                if(v.getOnway()==3) v.setOnway(4);
            }
        }
        else return;
        Highway.updateCar(CurrentTime.getCurrentTime());
    }
}

class OrderN implements Command {
    @Override
    public void execute(StringTokenizer st, Time time){
        int num=Integer.parseInt(st.nextToken());
        for(Vehicle v: VehicleList.getVehicleList()){
            if(v.getNumber()==num){
                if(v.getOnway()==1||v.getOnway()==2) { System.out.println("[error - Aleardy running vehicle!]"); return; }
                EnterGate.enterVehicle(v, st.nextToken(), st.nextToken(), time);
                return;
            }
        }
        System.out.println("[error - vehicle number does not exist!]");
    }
}

class OrderO implements Command {
    @Override
    public void execute(StringTokenizer st, Time time){
        int count=0;
        for(Vehicle v: VehicleList.getVehicleList()){
            if(v.getOnway()==1||v.getOnway()==2){
                count++;
                System.out.println(count+". "+v.o());
            }
        }
        if(count==0) System.out.println("통행 차량이 없습니다!");
    }
}

class OrderX implements Command {
    @Override
    public void execute(StringTokenizer st, Time time){
        int count=0;
        for(Vehicle v: VehicleList.getVehicleList()){
            if(v.getOnway()==3){
                count++;
                System.out.println(count+". "+v.x());
            }
        }
        if(count==0) System.out.println("진출한 차량이 없습니다!");
    }
}

class OrderR implements Command {
    @Override
    public void execute(StringTokenizer st, Time time){
        VehicleList.registerVehicle(st.nextToken(), Integer.parseInt(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));
    }
}

class OrderError implements Command {
    @Override
    public void execute(StringTokenizer st, Time time){
        System.out.println("[error - wrong command character!]");
    }
}

class VehicleList {
    private static int current;
    private static Vehicle[] vehicleList;
    private static Vehicle[] tempList;

    public static void setVehicleList(BufferedReader br) throws IOException {
        current = Integer.parseInt(br.readLine());
        vehicleList = new Vehicle[current];
        StringTokenizer st = null;
        for(int i=0;i<current;i++){
            st = new StringTokenizer(br.readLine());
            switch(st.nextToken()){
                case "g": vehicleList[i] = new GasCar(Integer.parseInt(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken())); break;
                case "b": vehicleList[i] = new Bus(Integer.parseInt(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken())); break;
                case "t": vehicleList[i] = new Truck(Integer.parseInt(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken())); break;
                case "h": vehicleList[i] = new HybridCar(Integer.parseInt(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken())); break;
                default: break;
            }
        }
    }
    public static Vehicle[] getVehicleList(){ return vehicleList; }
    public static void registerVehicle(String type, int n,double d,double s){
        for(Vehicle v: vehicleList){
            if(v.getNumber()==n) {
                System.out.println("[error - already registered vehicle number!]");
                return;
            }
        }
        current++;
        tempList = new Vehicle[current];
        for(int i=0;i<current-1;i++){
            tempList[i]=vehicleList[i];
        }
        if(type.equals("g")) tempList[current-1]=new GasCar(n,d,s);
        else if(type.equals("b")) tempList[current-1]=new Bus(n,d,s);
        else if(type.equals("t")) tempList[current-1]=new Truck(n,d,s);
        else if(type.equals("h")) tempList[current-1]=new HybridCar(n,d,s);
        vehicleList=tempList;
    }
}

abstract class Vehicle{
    private final int number;
    private final double speed;
    private int onway=0;
    private Time enterTime;
    private int enterDistance;
    private Time exitTime;
    private int exitDistance;
    private int distance;
    private int position;
    private int fee;

    Vehicle(int number, double speed){
        this.number=number; this.speed=speed;
    }

    public int getNumber(){ return number; }
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
    public String o(){
        return getType()+" "+getNumber()+" "+getPosition()+"km";
    }
    public String x(){
        return getType()+" "+getNumber()+" "+getExitTime()+" "+getFee()+"원";
    }

    abstract public String getType();
    abstract public void calcFee(double basicFee, double distanceRate);
}

class GasCar extends Vehicle {
    private final double displacement;
    
    GasCar(int number, double speed, double displacement){
        super(number,speed); this.displacement=displacement;
    }

    public double getDisplacement(){ return displacement; }
    @Override
    public String getType(){ return "가솔린차"; }
    @Override
    public void calcFee(double basicFee, double distanceRate){
        double displacementRate;
        if(displacement<1000) displacementRate=0.8;
        else if(displacement<2000) displacementRate=1.0;
        else if(displacement<3000) displacementRate=1.2;
        else displacementRate=1.5;
        setFee((int)((basicFee+getDistance()*distanceRate)*(getSpeed()/100)*displacementRate/10)*10);
    }
}

class Bus extends Vehicle {
    private final double max_passengers;

    Bus(int number, double speed, double max_passengers){
        super(number,speed); this.max_passengers=max_passengers;
    }

    public double getMax_passengers() { return max_passengers; }
    @Override
    public String getType(){ return "버스"; }
    @Override
    public void calcFee(double basicFee, double distanceRate){
        double passengerRate;
        if(max_passengers<12) passengerRate=0.8;
        else if(max_passengers<20) passengerRate=1.0;
        else if(max_passengers<30) passengerRate=1.2;
        else passengerRate=1.5;
        setFee((int)((basicFee+getDistance()*distanceRate)*(getSpeed()/100)*passengerRate/10)*10);
    }
}

class Truck extends Vehicle {
    private final double ton;

    Truck(int number, double speed, double ton){
        super(number,speed); this.ton=ton;
    }

    public double getTon(){ return ton; }
    @Override
    public String getType(){ return "트럭"; }
    @Override
    public void calcFee(double basicFee, double distanceRate){
        double tonRate;
        if(ton<1) tonRate=0.8;
        else if(ton<2) tonRate=1.0;
        else if(ton<4) tonRate=1.2;
        else tonRate=1.5;
        setFee((int)((basicFee+getDistance()*distanceRate)*(getSpeed()/100)*tonRate/10)*10);
    }
}

class HybridCar extends Vehicle {
    private final double displacement;
    
    HybridCar(int number, double speed, double displacement){
        super(number,speed); this.displacement=displacement;
    }

    public double getDisplacement(){ return displacement; }
    @Override
    public String getType(){ return "하이브리드차"; }
    @Override
    public void calcFee(double basicFee, double distanceRate){
        double displacementRate;
        if(displacement<1000) displacementRate=0.8;
        else if(displacement<2000) displacementRate=1.0;
        else if(displacement<3000) displacementRate=1.2;
        else displacementRate=1.5;
        setFee((int)((basicFee+getDistance()*distanceRate)*(getSpeed()/100)*displacementRate/2/10)*10);
    }
}

class EnterGate {

    public static boolean checkPoint(String p1, String p2){
        if(p1.equals(p2)) { System.out.println("[error - entry point and exit point must be different!]"); return false; }
        if((p1.equals("서울")||p1.equals("수원")||p1.equals("대전")||p1.equals("대구")||p1.equals("부산"))==false) { System.out.println("[error - entry point should be included in list!]"); return false; }
        if((p2.equals("서울")||p2.equals("수원")||p2.equals("대전")||p2.equals("대구")||p2.equals("부산"))==false) { System.out.println("[error - exit point should be included in list!]"); return false; }
        return true;
    }
    public static void enterVehicle(Vehicle v,String p1,String p2,Time t){
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
                v.setDistance(d2-d1);
                v.setOnway(1);
            }
            else{
                v.setDistance(d1-d2);
                v.setOnway(2);
            }
            v.setPoint(d1,d2);
            v.setPosition(d1);
            v.setEnterTime(t);
            v.setExitTime(t.plusTime((int)(v.getDistance()/v.getSpeed()*60)));     
        }
    }
}

class ExitGate {
    private static int basicFee;
    private static int distanceRate;
    
    public static void setExitGate(BufferedReader br) throws IOException {
        StringTokenizer st = new StringTokenizer(br.readLine());
        ExitGate.basicFee=Integer.parseInt(st.nextToken()); ExitGate.distanceRate=Integer.parseInt(st.nextToken());
    }
    public static void exitVehicle(Vehicle v){
        v.setOnway(3);
        v.calcFee(basicFee,distanceRate);     
    }
}

class Highway {

    public static void updateCar(Time t){
        for(Vehicle v: VehicleList.getVehicleList()){
            if(v.getOnway()==1||v.getOnway()==2){
                int migration = (int)(t.getInterval(v.getEnterTime(), t)*v.getSpeed()/60.0);
                if(migration<v.getDistance()){
                    if(v.getOnway()==1){
                        v.setPosition(v.getEnterDistatnce()+migration);
                    }
                    else{
                        v.setPosition(v.getEnterDistatnce()-migration);
                    }
                }
                else{
                    ExitGate.exitVehicle(v);
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

class CurrentTime {
    private static Time time;

    public static void setCurrentTime(Time time){
        CurrentTime.time = time;
    }
    public static Time getCurrentTime(){ return time; }
}
