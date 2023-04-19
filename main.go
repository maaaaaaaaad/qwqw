package main

import (
	"fmt"
	"strconv"
)

func main() {
	var naturalNum int
	var discountRate float64

	for {
		fmt.Print("첫 번째 숫자(자연수)를 입력하세요: ")
		input, err := strconv.Atoi(getUserInput())

		if err != nil || input <= 0 {
			fmt.Println("잘못된 입력입니다. 자연수를 입력하세요.")
			continue
		}

		naturalNum = input
		break
	}

	for {
		fmt.Print("두 번째 숫자(할인율)를 입력하세요: ")
		input, err := strconv.ParseFloat(getUserInput(), 64)

		if err != nil || input <= 0 {
			fmt.Println("잘못된 입력입니다. 양의 실수를 입력하세요.")
			continue
		}

		discountRate = input
		break
	}

	discount := float64(naturalNum) * (discountRate / 100)
	payment := float64(naturalNum) - discount
	result := int(payment)
	fmt.Printf("최종 지불 금액은 %d 입니다.", result)
}

func getUserInput() string {
	var input string
	fmt.Scanln(&input)
	return input
}
