from __future__ import annotations

import pendulum

from airflow.models.dag import DAG
from airflow.operators.bash import BashOperator
from airflow.operators.python import PythonOperator

# =============================================================================
# ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ Transform & Load 단계에서 사용할 Python 함수 ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
# 이 함수는 Airflow의 작업자(Worker) 환경에서 실행됩니다.
# =============================================================================
def predict_congestion_and_load():
    """
    RDS의 원시 데이터 테이블에서 데이터를 읽어와 ML로 혼잡도를 예측하고,
    그 결과를 새로운 테이블에 저장하는 함수입니다.
    """
    # (주의: 이 함수가 실제로 작동하려면, Airflow Worker에
    # DB 접속 라이브러리(psycopg2-binary)와 ML 라이브러리(scikit-learn, pandas)가
    # 설치되어 있어야 합니다. 이것은 다음 단계에서 진행합니다.)

    print("TRANSFORM & LOAD: 데이터 변환 및 예측을 시작합니다...")

    # ----- 1. 데이터베이스에서 원시 데이터 읽어오기 (Transform의 시작)-----
    # (실제로는 여기에 psycopg2를 사용하여 'raw_traffic_data' 테이블에서
    # 최신 데이터를 SELECT하는 코드가 들어갑니다.)
    print("TRANSFORM: RDS의 raw_traffic_data 테이블에서 데이터를 성공적으로 읽어왔습니다.")
    raw_data = [
        {'place_name': '강남역', 'avg_road_speed': '15.5'},
        {'place_name': '광화문', 'avg_road_speed': '25.2'}
    ] # <- DB에서 읽어온 데이터 예시

    # ----- 2. 머신러닝 모델로 예측하기 -----
    # (실제로는 여기에 저장된 ML 모델 파일을 로드하고,
    # raw_data를 입력하여 혼잡도를 예측하는 코드가 들어갑니다.)
    print("TRANSFORM: ML 모델로 혼잡도 예측을 수행합니다.")
    predictions = [
        {'place_name': '강남역', 'congestion': 'red'},
        {'place_name': '광화문', 'congestion': 'yellow'}
    ] # <- ML 예측 결과 예시

    # ----- 3. 최종 테이블에 결과 저장하기 (Load) -----
    # (실제로는 여기에 psycopg2를 사용하여 'congestion_forecast' 테이블에
    # 예측 결과(predictions)를 INSERT하는 코드가 들어갑니다.)
    print("LOAD: 예측 결과를 congestion_forecast 테이블에 성공적으로 저장했습니다.")
    print("TRANSFORM & LOAD: 모든 작업이 완료되었습니다.")

# =============================================================================
# ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ DAG 정의 ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
# =============================================================================
with DAG(
    dag_id='traffic_data_etl_pipeline',
    start_date=pendulum.datetime(2025, 1, 1, tz="Asia/Seoul"),
    schedule='0 23,9 * * *', # KST 08시, 18시
    catchup=False,
    tags=['traffic', 'etl', 'ml'],
) as dag:
    # --- Task 1: Extract ---
    # Java 앱을 실행하여 API 데이터를 'raw_traffic_data' 테이블에 저장합니다.
    extract_raw_data = BashOperator(
        task_id='extract_raw_data',
        bash_command='java -Dspring.datasource.password={{ conn.my_rds_db.password }} -jar /opt/airflow/app/traffic-prediction-*.jar',
    )

    # --- Task 2: Transform & Load ---
    # Python 함수를 실행하여 원시 데이터를 가공/예측하고, 최종 테이블에 저장합니다.
    transform_and_load_data = PythonOperator(
        task_id='transform_and_load_data',
        python_callable=predict_congestion_and_load,
    )

    # --- 파이프라인 순서 정의 ---
    # "extract_raw_data 작업이 성공해야만, transform_and_load_data 작업을 실행하라"
    extract_raw_data >> transform_and_load_data