from __future__ import annotations
import pendulum
from airflow.models.dag import DAG
from airflow.operators.bash import BashOperator
from airflow.operators.python import PythonOperator

# =============================================================================
# ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ Transform & Load 단계에서 사용할 Python 함수 ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
# 이 함수는 Airflow의 작업자(Worker) 환경에서 실행됩니다.
# =============================================================================
def etl_prediction_data():
    """
    이것은 앞으로 우리가 채워나갈 'Transform & Load' 단계의 뼈대입니다.
    1. RDS의 'raw_traffic_data'에서 최신 데이터를 Extract.
    2. ML 모델로 혼잡도를 예측(Transform).
    3. 예측 결과를 'prediction_results' 테이블에 Load.
    """
    print("ETL Prediction: Start processing raw data...")
    # 여기에 psycopg2, pandas, scikit-learn을 사용한
    # 데이터 처리 코드가 들어갈 예정입니다.
    # 지금은 성공적으로 실행되었다는 메시지만 남깁니다.
    print("ETL Prediction: Finished loading prediction results.")

# =============================================================================
# ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ DAG 정의 ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
# =============================================================================
with DAG(
    dag_id='traffic_data_etl_pipeline', # DAG의 고유한 이름
    start_date=pendulum.datetime(2025, 9, 29, tz="Asia/Seoul"), # DAG 유효 시작 날짜
    description='서울시 핫스팟 교통/날씨 데이터를 수집하고 혼잡도를 예측하는 ETL 파이프라인',
    schedule='0 8,18 * * *', # 한국 시간(KST) 오전 8시와 오후 6시에 실행
    catchup=False, # 과거에 놓친 스케줄을 한꺼번에 실행하지 않음
    tags=['traffic', 'etl', 'ml'],
) as dag:
    
    # --- Task 1: Extract ---
    # Java 앱을 실행하여 API 데이터를 'raw_traffic_data' 테이블에 저장합니다.
    task_extract_raw_data = BashOperator(
        task_id='extract_raw_data_from_api',
        # Airflow Connection에 저장된 DB 비밀번호를 안전하게 사용합니다.
        bash_command='java -Dspring.datasource.password={{ conn.postgres_default.password }} -jar /opt/airflow/app/traffic-prediction-*.jar',
    )

    # --- Task 2: Transform & Load ---
    # Python 함수를 실행하여 원시 데이터를 가공/예측하고, 최종 테이블에 저장합니다.
    task_transform_and_load = PythonOperator(
        task_id='transform_and_load_prediction',
        python_callable=etl_prediction_data,
    )

    # --- 파이프라인 순서 정의 ---
    # "extract_raw_data 작업이 성공해야만, transform_and_load 작업을 실행하라"
    task_extract_raw_data >> task_transform_and_load