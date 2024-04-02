package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    private static String WEB_DRIVER_PATH = "C:\\Users\\joe04\\chromedriver-win64\\chromedriver.exe"; //크롬웹드라이버 경로 지정

    public static void main(String[] args) throws InterruptedException {
        // 아이디 비밀번호 입력
        Scanner scanner = new Scanner(System.in);
        System.out.print("ID : ");
        String inputID = scanner.nextLine();
        System.out.print("PW : ");
        String inputPW = scanner.nextLine();
        // Chrome WebDriver 경로 설정
        System.setProperty("webdriver.chrome.driver", WEB_DRIVER_PATH);

        // Chrome WebDriver 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); // 브라우저 창을 최대화

        // WebDriver 인스턴스 생성
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); // 요소가 로딩될 때까지 대기할 최대 시간 설정

        BufferedWriter writer = null;
        try {
            // CSV 파일 생성

            writer = new BufferedWriter(new FileWriter("lecture_info.csv"));
            writer.write("전공/계열\t학년\t이수구분\t인재상\t학수번호\t교과목명\t학점\t교수\t교시\t강의실(건물명)\t인원\t강의계획서\t강의평가\t비고\n");

            // 한신대 수강신청 로그인 페이지로 이동
            driver.get("https://sugang.hs.ac.kr/login");

            // 로그인 정보 입력
            WebElement usernameInput = driver.findElement(By.id("id")); // 아이디 입력란
            WebElement passwordInput = driver.findElement(By.id("pass")); // 비밀번호 입력란
            WebElement loginButton = driver.findElement(By.xpath("//input[@id='btnLogin' and @class='btn-login']")); //로그인 버튼

            usernameInput.sendKeys(inputID);    // 사용자_아이디
            passwordInput.sendKeys(inputPW);    // 사용자_비밀번호
            loginButton.click();

            // 교과목 선택 페이지로 이동
            Thread.sleep(2500);
            driver.get("https://sugang.hs.ac.kr/course/subject");

            WebElement departmentDropdown = driver.findElement(By.id("sch_dept"));
            Select select = new Select(departmentDropdown);
            WebElement searchButton = driver.findElement(By.id("btn_search"));

            for (int i = 0; i < 3; i++) { // numberOfOptions
                // 학과 선택
                WebElement selectedOption = select.getFirstSelectedOption();
                String department = selectedOption.getText();
                select.selectByIndex(i);

                // 조회 버튼 클릭
                searchButton.click();

                // 페이지가 로딩되는 동안 잠시 대기
                Thread.sleep(4000);

                // 테이블 읽기
                WebElement table = driver.findElement(By.cssSelector("table.table-type01"));
                List<WebElement> rows = table.findElements(By.tagName("tr"));

                // 테이블 행 가져오기
                for (int j = 1; i < rows.size(); j++) { // 첫 번째 행은 헤더이므로 건너뜀
                    WebElement row = rows.get(j);
                    List<WebElement> cells = row.findElements(By.tagName("td"));
                    StringBuilder rowData = new StringBuilder();
                    boolean firstCell = true;
                    rowData.append(department).append("\t");
                    // 테이블 셀 가져오기
                    for (WebElement cell : row.findElements(By.tagName("td"))) {
                        if (!firstCell) {
                            // 셀 내용을 CSV 형식으로 저장
                            rowData.append(cell.getText()).append("\t");
                        }
                        firstCell = false;
                    }
                    // 행 데이터를 CSV 파일에 저장
                    writer.write(rowData.substring(0, rowData.length() - 1) + "\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // WebDriver 종료
            driver.quit();

            // 파일 닫기
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
