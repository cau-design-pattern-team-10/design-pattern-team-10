# 소프트웨어 디자인 문서

## 프로젝트 내용 요약
- 개념설계 및 UseCase 정리
- UI와 Model 의 분리: - "모듈 구조", "기존 코드에서의 설계 변경 사항" 참고
  - 화면상의 좌표(java.awt.Point)와 개념상의 좌표(com.holub.life.model.Point) 분리
  - 옵저버 패턴을 통한 모델변화만을 통해 화면 갱신 가능
- `figureNextState()` 메서드 리펙토링 및 `Direction` 클래스 삭제 - "기존 코드에서의 설계 변경 사항" 참고
- 확장기능(E-Undo, E-Load Overlap, E-상태 표시) 추가 - "Usecase - 확장기능" 참고

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
    - 게임 진행 상태에 따라, 현실 시간의 일정시간을 1틱으로 동작한다.
  - 1틱이 증가할때 그리드에 규칙을 적용하며, 이를 1틱 진행한다라고 말할 수 있다.
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
  - Grid 내의 모든 Cell 이 규칙에 맞추어 1 tick 진행한다.
  - 게임 진행 상태는 유지한다.

- Run Agonizing
  - 게임 진행 상태를 Agonizing 으로 변경한다.
  - 500 ms 를 1 tick 으로 설정한다.

- Run Slow
  - 게임 진행 상태를 Slow 으로 변경한다.
  - 150 ms 를 1 tick 으로 설정한다.

- Run Medium
  - 게임 진행 상태를 Medium 으로 변경한다.
  - 70 ms 를 1 tick 으로 설정한다.

- Run Fast
  - 게임 진행 상태를 Fast 으로 변경한다.
  - 30 ms 를 1 tick 으로 설정한다.

- Halt
  - 게임 진행 상태를 Halt 로 변경한다.
  - 현실 시간에 따른 tick 진행을 정지한다.

#### 확장기능
- E-Undo : 이전에 Tick 을 되돌린다.
- E-Load Overlap : 기존의 보드를 유지한 채 파일을 불러와 활성화된 셀을 덮어씌운다.
- E-상태 표시 : 현재 게임의 진행상태를 표시한다.

### 모듈 구조
- 모듈은 크게 UI 와 Model 로 구별된다.
- 기본 설계 원칙은 다음과 같다.:
  - UI:
    - UniverseUI를 제외한 모든 UI 는 상위 UI(이하 parent)를 가지며, 자신이 다시 그려져야하는 상황에서 parent의 `repaint()` 또는 `update()` 함수를 호출하여 자신의 변화를 알리며, 직접 그리지 않는다.
    - UniverseUI를 제외한 모든 UI 는 `Observer` 이며 `Observable` 이다.
    - 모든 UI Component 는 `repaint()` 또는 `update()` 함수가 호출되었을 때, 자신이 보유하고 있는 객체를 갱신하여 화면에 반영한다.
  - Model:
    - Model과 UI 는 Observer 패턴으로 연결되며, Model 을 변경하고 UI에 반영되기를 원한다면 `update()` 를 호출한다.

### 모듈 간의 관계
- Universe의 구성요소는 `TickSystem`, `Clock`, `Neighborhood` 로 구성된다.
- `TickSystem` 은 `Clock` 과 `UI` 간을 연결해주는 Mediator 이다.


### 기존 코드에서의 설계 변경 사항

- Cell 의 동작 방식 변경:
  - 기존 `Resident` 는 자신의 상태를 가져오기 위하여 옆 `Resident` 들이 필요하였으며, 이는 `Neighborhood` 와 `Direction`, `Resident` 클래스들간의 강결합을 유도하였다.
  - `ResidentService` 를 도입함으로써, 자신의 옆 Resident를 쉽게 획득할수 있게 만들었다.
  - `StateDiscriminator` 를 Visitor 패턴으로 도입하면서 Cell 의 `figureNextState()` 동작들을 한곳에서 모았다.
  - 이렇게 변경함에 따라 기존 존재하였던 edge 의 개념을 삭제하였으며, `Direction` 클래스가 삭제되었다.

- UI의 분리:
  - 기존에는 UI와 데이터가 동일한 객체에서 관리되고 있었으며, 좌표에 대한 개념이 UI와 강결합 되어 있었다.
  - Observer 패턴을 활용하여 이를 분리함에 따라 객체의 역할을 분리하였다.
