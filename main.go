package main

import (
	"fmt"
	"math"
	"strconv"
)

func main() {
	fmt.Print("전세금액을 입력하세요: ")
	leasePrice, err := strconv.Atoi(getUserInput())

	if err != nil || leasePrice <= 0 {
		fmt.Println("잘못된 입력입니다. 자연수를 입력하세요.")
		return
	}

	fmt.Print("연이율을 입력하세요: ")
	interestRate, err := strconv.ParseFloat(getUserInput(), 64)

	if err != nil || interestRate <= 0 {
		fmt.Println("잘못된 입력입니다. 양의 실수를 입력하세요.")
		return
	}

	monthlyInterest := calculateMonthlyInterest(leasePrice, interestRate)

	fmt.Printf("매달 지불해야 할 이자는 %.2f 원입니다.", monthlyInterest)
}

func calculateMonthlyInterest(leasePrice int, interestRate float64) float64 {
	monthlyInterestRate := interestRate / 12.0 / 100.0
	monthlyInterest := float64(leasePrice) * monthlyInterestRate
	monthlyInterest = math.Round(monthlyInterest)
	return monthlyInterest
}

func getUserInput() string {
	var input string
	fmt.Scanln(&input)
	return input
}
