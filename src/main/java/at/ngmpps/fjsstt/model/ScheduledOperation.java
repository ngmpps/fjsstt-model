package at.ngmpps.fjsstt.model;

public class ScheduledOperation {

	private int MachineId;
	private int JobId;
	private int OperationId;
	private int StartTime;
	private int EndTime;

	public ScheduledOperation() {
	}

	public ScheduledOperation(int MachineId, int JobId, int OperationId, int StartTime, int EndTime) {
		this.MachineId = MachineId;
		this.JobId = JobId;
		this.OperationId = OperationId;
		this.StartTime = StartTime;
		this.EndTime = EndTime;
	}

	public int getMachineId() {
		return MachineId;
	}

	public int getJobId() {
		return JobId;
	}

	public int getOperationId() {
		return OperationId;
	}

	public int getStartTime() {
		return StartTime;
	}

	public int getEndTime() {
		return EndTime;
	}

	public void setMachineId(int machineId) {
		MachineId = machineId;
	}

	public void setJobId(int jobId) {
		JobId = jobId;
	}

	public void setOperationId(int operationId) {
		OperationId = operationId;
	}

	public void setStartTime(int startTime) {
		StartTime = startTime;
	}

	public void setEndTime(int endTime) {
		EndTime = endTime;
	}

	public String toString() {
		return "ScheduledOperation {" + "machine='" + MachineId + '\'' + ", job='" + JobId + '\'' + ", operation='" + OperationId + '\''
				+ ", start='" + StartTime + '\'' + ", end=\'" + EndTime + '\'' + '}';
	}

}
