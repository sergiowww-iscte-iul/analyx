package pt.iscteiul.analyx.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import pt.iscteiul.analyx.exception.BusinessException;

import static pt.iscteiul.analyx.util.ControllerKeys.ERROR_MESSAGE;

@ControllerAdvice
public class ExceptionMapper {

	@ExceptionHandler(BusinessException.class)
	public ModelAndView handleBusinessException(
			BusinessException ex,
			HttpServletRequest request) {

		ModelAndView mv = new ModelAndView();

		// Add message to the model
		mv.addObject(ERROR_MESSAGE, ex.getMessage());

		// Reuse the view that the controller was rendering
		String viewName = getViewNameForRequest(request);
		mv.setViewName(viewName);

		return mv;
	}

	private String getViewNameForRequest(HttpServletRequest request) {
		// The attribute where Spring stores the resolved view name (from the controller)
		Object view = request.getAttribute("org.springframework.web.servlet.View");
		Object viewName = request.getAttribute("org.springframework.web.servlet.View.viewName");

		if (viewName != null) return viewName.toString();
		if (view != null) return view.toString();

		return "error"; // fallback
	}
}
