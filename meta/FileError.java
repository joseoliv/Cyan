package meta;

public enum FileError {

    ok_e("ok"),
    do_not_exist_e("file do not exist"),
    cannot_be_read_e("file cannot be read"),
    read_error_e("read error"),
    write_error_e("write error"),
    close_error_e("close error"),
    open_error_e("file cannot be opened"),
    file_not_found("file was not found"),
    file_should_take_parameters("file should take parameters"),
    file_should_not_take_parameters("file should not take parameters"),
    file_name_does_not_have_the_correct_name_format("do not have the correct file name format"),
    two_parameters_are_the_same("two parameters are the same"),
    package_not_found("package was not found");
	
	private FileError(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return message;
	}

	private String message;
}
