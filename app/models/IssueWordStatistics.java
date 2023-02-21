package models;

import java.util.Map;

public class IssueWordStatistics {
	public Map<String, Integer> wordfrequency;
	public IssueWordStatistics( Map<String, Integer> wordfrequency) {
		this.wordfrequency = wordfrequency;
	}
	public Map<String, Integer> getWordfrequency() {
		return wordfrequency;
	}
	public void setWordfrequency(Map<String, Integer> wordfrequency) {
		this.wordfrequency = wordfrequency;
	}
	
	
	
}