package dd.adv.trace.template.code;

import lombok.extern.slf4j.Slf4j;

/**
 * 템플릿 메소드 패턴
 *
 * abstract 클래스에 변하지 않는 기능의 뼈대(템플릿)를 두고,
 * 변하는 부분은 자식 클래스에서 상속을 통해 구현하도록 하는 방식
 *
 * [장점]
 * 1. 코드 중복 감소: 변하지 않는 핵심 로직을 부모 클래스에 모아 중복을 제거할 수 있다.
 * 2. 구조 강제: 자식 클래스들이 따라야 하는 전체적인 알고리즘 구조를 강제하여 일관성을 유지.
 * 3. 쉬운 구현: 상속을 이용하므로 비교적 간단하게 패턴을 구현하고 확장할 수 있다.
 *
 * [단점]
 * 1. 강한 결합: 상속을 사용하므로 부모-자식 클래스 간의 결합도가 높다.
 * 2. 런타임 유연성 부족: 컴파일 시점에 의존관계가 고정되어 런타임에 동적으로 로직을 변경하기 어렵다.
 * 3. 클래스 증가: 로직이 추가될 때마다 새로운 자식 클래스를 만들어야 한다.
 */

@Slf4j
public abstract class AbstractTemplate {

    public void execute() {
        long startTime = System.currentTimeMillis();
        //비즈니스 로직 실행
        call(); //상속
        //비즈니스 로직 종료
        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultTime={}", resultTime);
    }

    protected abstract void call();
}
