package io.ipstack.http;


/** Reason-phrases of HTTP status codes.
 */
public class HttpStatusCode {

	// No constructor is available; only static methods
	private HttpStatusCode() {
	}
	
	/** Gets the reason phrase of a given status code.
	  * @param code the status code
	  * @return the reason phrase of a given status code */
	public static String reasonOf(int code) {
		switch (code) {
			// 1xx 
			case 100 : return "Continue";
			case 101 : return "Switching Protocols";
			case 102 : return "Processing";
			case 103 : return "Early Hints";
		
			// 2xx successful
			case 200 : return "OK";
			case 201 : return "Created";
			case 202 : return "Accepted";
			case 203 : return "Non-Authoritative Information";
			case 204 : return "No Content";
			case 205 : return "Reset Content";
			case 206 : return "Partial Content";
			case 207 : return "Multi-Status";
			case 208 : return "Already Reported";
			case 226 : return "IM Used";
			
			// 3xx redirection
			case 300 : return "Multiple Choices";
			case 301 : return "Moved Permanently";
			case 302 : return "Found";
			case 303 : return "See Other";
			case 304 : return "Not Modified";
			case 305 : return "Use Proxy";
			case 306 : return "Switch Proxy";
			case 307 : return "Temporary Redirect";
			case 308 : return "Permanent Redirect";
			
			// 4xx request failure
			case 400 : return "Bad Request";
			case 401 : return "Unauthorized";
			case 402 : return "Payment Required";
			case 403 : return "Forbidden";
			case 404 : return "Not Found";
			case 405 : return "Method Not Allowed";
			case 406 : return "Not Acceptable";
			case 407 : return "Proxy Authentication Required";
			case 408 : return "Request Timeout";
			case 409 : return "Conflict";
			case 410 : return "Gone";
			case 411 : return "Length Required";
			case 412 : return "Precondition Failed";
			case 413 : return "Payload Too Large";
			case 414 : return "URI Too Long";
			case 415 : return "Unsupported Media Type";
			case 416 : return "Range Not Satisfiable";
			case 417 : return "Expectation Failed";
			case 418 : return "I'm a teapot";
			case 421 : return "Misdirected Request";
			case 422 : return "Unprocessable Entity";
			case 423 : return "Locked";
			case 424 : return "Failed Dependency";
			case 425 : return "Too Early";
			case 426 : return "Upgrade Required";
			case 428 : return "Precondition Required";
			case 429 : return "Too Many Requests";
			case 431 : return "Request Header Fields Too Large";
			case 451 : return "Unavailable For Legal Reasons";
			
			// 5xx server failure
			case 500 : return "Internal Server Error";
			case 501 : return "Not Implemented";
			case 502 : return "Bad Gateway";
			case 503 : return "Bad Gateway";
			case 504 : return "Gateway Timeout";
			case 505 : return "HTTP Version Not Supported";
			case 506 : return "Variant Also Negotiates";
			case 507 : return "Insufficient Storage ";
			case 508 : return "Loop Detected";
			case 510 : return "Not Extended";
			case 511 : return "Network Authentication Required";
		}	
		// else     
		switch (code/100) {
			// 1xx Provisional
			case 1 : return "Provisional";
			// 2xx Successful
			case 2 : return "Successful";
			// 3xx Redirection
			case 3 : return "Redirection";
			// 4xx Request Failure
			case 4 : return "Request Failure";
			// 5xx Server Failure
			case 5 : return "Server Failure";
			// 6xx Global Failures
			case 6 : return "Global Failures";
		}
		// else, it is not valid
		return null;
	}
	
}
