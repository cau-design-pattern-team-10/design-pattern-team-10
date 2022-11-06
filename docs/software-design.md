# 소프트웨어 디자인 문서

## 개념 설계
### 시스템 구조
- 용어정리:
  - 칸(cell) 은 Game of Life 의 가장 작은 단위를 말한다.
  - 칸은 켜짐(1), 꺼짐(0) 의 상태가 있다.
  - 영역(area)은 8x8 의 칸이 합쳐서 생긴 단위이다.
  - 하나의 칸은 하나의 영역에만 소속된다.
  - 그리드(Grid, 또는 보드, board) 는 8x8 의 영역을 의미한다.
  - 게임은 그리드 1개로만 구성된다.
  - 규칙은 Conway's Game of Life 의 규칙을 말하며, 해당 [문서](http://www.radicaleye.com/lifepage/)를 따른다.
  - 틱(Tick) 은 게임의 최소 시간단위를 의미한다.
  - TODO: Tick 이 실제로 어떻게 흐르는지 확인해야함
  - 스텝(Step)은 Game of Life 의 규칙을 1회 적용한 것을 의미한다.
  - 게임 진행 상태(또는 진행 상태, State) 는 게임의 진행 상태를 의미하며, Halt, Agonizing, Slow, Medium, Fast 가 존재한다.
  - 게임은 반드시 게임 진행 상태를 가진다.
  - 최초 시작시 게임은 아래와 같은 상태를 가진다.:
    - 진행상태: Halt
    - 그리드의 모든 칸은 꺼져있다.
  - 메뉴는 게임 실행시 상단에 존재한다.
  - 메뉴에는 Grid와 Go가 존재한다.:
    - Grid 는 게임의 Grid 상태를 변경할수 있는 메뉴와 게임 외부적인 기능를 가진다.
    - Go 는 게임 진행 상태를 변경할수 있는 메뉴를 가진다.

### Usecase
#### 기본기능
- Clear Board :
  - 현재 게임의 상태를 초기화한다.
  - 게임의 진행상태(Halt, Agonizing, Slow, Medium, Fast) 와는 별도로 동작하여, 게임의 진행상태가 변경되지 않는다.

- Load Board:
  - 저장된 상태의 보드를 불러온다.
  - 게임의 진행상태가 Halt 로 변경된다.

- Save Board:
  - 현재 게임의 상태를 저장한다.
  - 게임의 진행상태가 Halt 로 변경된다.

- Exit:
  - 게임이 종료된다.

- Click cell:
  - 조작한 칸의 켜짐여부(0 또는 1) 을 변경한다.
  - 조작한 칸이 속한 영역의 테두리가 표시된다.:
    - 켜져있던 칸을 꺼서, 영역내의 모든 칸이 꺼져있더라도 테두리가 표시된다.

- Run single step:
  - Grid 내의 모든 Cell 이 규칙에 맞추어 1 step 진행한다.
  - 게임 진행 상태는 유지한다.

- Run Agonizing
  - 게임 진행 상태를 Agonizing 으로 변경한다.
  - 500 tick마다 1 step 진행한다.

- Run Slow
  - 게임 진행 상태를 Slow 으로 변경한다.
  - 150 tick마다 1 step 진행한다.

- Run Medium
  - 게임 진행 상태를 Medium 으로 변경한다.
  - 70 tick마다 1 step 진행한다.

- Run Fast
  - 게임 진행 상태를 Fast 으로 변경한다.
  - 30 tick마다 1 step 진행한다.

- Halt
  - 게임 진행 상태를 Halt 로 변경한다.

#### 확장기능

### 모듈 구조
- <모듈 이름>:
  - <기능 설명>

### 모듈 간의 관계
### 자료 설계
- <파일 구조 설명>

## 상세 설계
### 모듈 내부 설계
### 사용자 인터페이스