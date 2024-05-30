import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class HCMS{

    public static void main(String[] args) throws IOException {
        //입력
        Scanner scan = new Scanner(new File("hcms.txt"));

        //기본 요금 및 거리요율
        double bsFee = scan.nextDouble();
        double disRt = scan.nextDouble();
        scan.nextLine();

        //차량 수(vNum)
        int vNum = scan.nextInt();
        CAR[] CarList = new CAR[vNum];

        //차량 번호 배기량 속도
        for(int i=0;i<vNum;i++){
            scan.nextLine();
            CarList[i] = new CAR(scan.nextInt(), scan.nextInt(), scan.nextInt());
        }

        scan.close();

        //현재시각 설정
        TIME time = new TIME(2024,03,20,21,00);
        int stand = 113760;

        String test; TIME cur; int number;
        Scanner in = new Scanner(new File("in.txt"));
        while(true){
            test=null;
            try {
            test = in.next();
            } catch(Exception e) {
                System.out.println("error: 명령 약자를 입력하세요."); in.nextLine(); continue;
            }
            //t
            if(test.equals("t")){
                try {
                    cur = new TIME(in.nextInt(),in.nextInt(),in.nextInt(),in.nextInt(),in.nextInt());
                } catch (Exception e) {
                    System.out.println("error: 잘못된 시간 입력"); in.nextLine(); continue;
                }
                //오류 처리
                //월, 일 숫자 유효한지 판단
                if(cur.year%4==0&&cur.year%100!=0){
                    switch(cur.mon){
                        case 1,3,5,7,8,10,12:
                        if(1>cur.date||cur.date>31) {System.out.println("error: 잘못된 시간 입력"); in.nextLine(); continue;}
                        else break;
                        case 4,6,9,11:
                        if(1>cur.date||cur.date>30) {System.out.println("error: 잘못된 시간 입력"); in.nextLine(); continue;}
                        else break;
                        case 2:
                        if(1>cur.date||cur.date>29) {System.out.println("error: 잘못된 시간 입력"); in.nextLine(); continue;}
                        else break;
                        default: System.out.println("error: 잘못된 시간 입력"); in.nextLine(); continue;
                    }
                } else{
                    switch(cur.mon){
                        case 1,3,5,7,8,10,12:
                        if(1>cur.date||cur.date>31) {System.out.println("error: 잘못된 시간 입력"); in.nextLine(); continue;}
                        else break;
                        case 2,4,6,9,11:
                        if(1>cur.date||cur.date>30) {System.out.println("error: 잘못된 시간 입력"); in.nextLine(); continue;}
                        else break;
                        default: System.out.println("error: 잘못된 시간 입력"); in.nextLine(); continue;
                    }
                }
                LocalDateTime a = LocalDateTime.of(time.year, time.mon, time.date, time.hour, time.min);
                LocalDateTime b = LocalDateTime.of(cur.year, cur.mon, cur.date, cur.hour, cur.min);
                Duration dur1 = Duration.between(a, b);
                long durmin1 = dur1.toMinutes();
                if(durmin1<=0) {System.out.println("error: 잘못된 시간 입력"); in.nextLine(); continue;}
                //시간 설정
                else{
                    time=cur;
                    //위치, 진출여부, 통행료 계산
                    for(CAR c: CarList)
                    {
                        if(c.onway==1||c.onway==2){
                            LocalDateTime s = LocalDateTime.of(c.stime.year, c.stime.mon, c.stime.date, c.stime.hour, c.stime.min);
                            LocalDateTime now = LocalDateTime.of(time.year, time.mon, time.date, time.hour, time.min);
                            Duration dur = Duration.between(s, now);
                            long durmin = dur.toMinutes();
                            c.dt=(double) durmin/60*c.cvel;
                            if(c.dt>=c.delp){
                                c.onway=3;
                                c.fee=(int)(bsFee+(c.delp*disRt))*c.cvel/100*c.cdis/2000;
                                c.fee=(int)(c.fee/10)*10;
                            }
                            else if(c.dt<c.delp){
                                if(c.onway==1){
                                    c.pos=c.pos1+c.dt;
                                }
                                else if(c.onway==2){
                                    c.pos=c.pos1-c.dt;
                                }
                            }
                        }
                        }
                    }
                    in.nextLine();
                    continue;
                }

            //n
            else if(test.equals("n")){
                try {
                    number=in.nextInt();
                } catch(Exception e) {
                    System.out.println("error: 차량번호가 입력되어야 합니다."); in.nextLine(); continue;
                }
                int co=0;
                for(CAR c: CarList){
                    if(c.cnum==number){
                        co++;
                        if(c.onway==0){
                            c.stime=time;
                            c.spoint=in.next(); c.epoint=in.next();
                            switch(c.spoint){
                                case "서울": c.pos1=0; break;
                                case "수원": c.pos1=30; break;
                                case "대전": c.pos1=130; break;
                                case "대구": c.pos1=290; break;
                                case "부산": c.pos1=400; break;
                                default: System.out.println("error: 잘못된 출발 위치"); in.nextLine(); continue;
                            }
                            c.pos=c.pos1;
                            switch(c.epoint){
                                case "서울": c.pos2=0; break;
                                case "수원": c.pos2=30; break;
                                case "대전": c.pos2=130; break;
                                case "대구": c.pos2=290; break;
                                case "부산": c.pos2=400; break;
                                default: System.out.println("error: 잘못된 도착 위치"); in.nextLine(); continue;
                            }
                            c.delp=c.pos2-c.pos1;
                            //분단위 소요시간
                            c.deltime=(int)c.delp/c.cvel*60;
                            LocalDateTime now = LocalDateTime.of(time.year, time.mon, time.date, time.hour, time.min);
                            LocalDateTime end = now.plusMinutes(c.deltime);
                            c.etime.year=end.getYear(); c.etime.mon=end.getMonthValue(); c.etime.date=end.getDayOfMonth(); c.etime.hour=end.getHour(); c.etime.min=end.getMinute();
                            if(c.delp>0) c.onway=1;
                            else if(c.delp<0) {c.delp=-c.delp; c.onway=2;}
                            else if(c.delp==0) continue;
                            }
                            else System.out.println("고속도로 진입 전인 차량만 진입시킬 수 있습니다.");
                        }
                    }
                    if(co==0) System.out.println("입력된 차량 번호는 목록에 없습니다.");
                    in.nextLine();
                    continue;       
                }

            //o
            else if(test.equals("o")){
                System.out.println("현재시간: "+time.year+"/"+time.mon+"/"+time.date+"-"+time.hour+":"+time.min);
                int count=0; 
                for(CAR c: CarList){
                    if(c.onway==1||c.onway==2){
                        count++;
                        System.out.println(count+". "+c.cnum+" "+c.cdis+"cc "+c.cvel+"km "+c.spoint+"->"+c.epoint+" "+c.stime.year+"/"+c.stime.mon+"/"+c.stime.date+"-"+c.stime.hour+":"+c.stime.min+" 위치:"+c.pos+"km");
                    }
                    else continue;
                    }
                if(count==0) System.out.println("에 통행 차량이 없습니다!");
                in.nextLine();
                continue;
                }

            //x
            else if(test.equals("x")){
                System.out.println("현재시간: "+time.year+"/"+time.mon+"/"+time.date+"-"+time.hour+":"+time.min);
                int count=0;
                for(CAR c: CarList){
                    if(c.onway==3){
                        count++;
                        System.out.println(count+". "+c.cnum+" "+c.cdis+"cc "+c.cvel+"km "+c.spoint+"->"+c.epoint+" "+c.stime.year+"/"+c.stime.mon+"/"+c.stime.date+"-"+c.stime.hour+":"+c.stime.min+" "+c.etime.year+"/"+c.etime.mon+"/"+c.etime.date+"-"+c.etime.hour+":"+c.etime.min+" "+c.fee+"원");
                    }
                    else continue;
                    }
                if(count==0) System.out.println("진출한 차량이 없습니다!");
                continue;                           
            }

            //q
            else if(test.equals("q")){
                in.close();
                System.exit(0);
            }
            else { System.out.println("error: 잘못된 명령 약자"); continue;}
        }
    }
}

class TIME {
    int year;
    int mon;
    int date;
    int hour;
    int min;

    TIME(int year,int mon,int date){
        this.year=year; this.mon=mon; this.date=date;
    }
    TIME(int hour,int min){
        this.hour=hour; this.min=min;
    }
    TIME(int year,int mon,int date,int hour,int min){
        this.year=year; this.mon=mon; this.date=date; this.hour=hour; this.min=min;
    }
}

class CAR {
    int cnum; int cdis; int cvel;
    double pos; double pos1; double pos2; double delp; double rep; double dt;
    int deltime; TIME stime; TIME etime;
    int onway;
    int fee;
    String spoint; String epoint;

    CAR(int num, int dis, int vel){
        cnum=num; cdis=dis; cvel=vel; pos=0; pos1=0; pos2=0; delp=0; rep=0; dt=0; stime=null; deltime=0; etime= new TIME(0,0); onway=0; fee=0;
    }
}
