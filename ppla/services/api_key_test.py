from langchain_groq import ChatGroq
from dotenv import load_dotenv
import os


load_dotenv()

print(os.getenv("GROQ_API_KEY"))

def test_groq():
    llm = ChatGroq(
        model="llama-3.3-70b-versatile",
        groq_api_key=os.getenv("GROQ_API_KEY"),
        temperature=0.7
    )
    
    
    result = llm.invoke("What is the capital of France?")
    print(result.content)
    
test_groq()