package pers.hsc.evats.modules.email.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pers.hsc.evats.core.common.controller.BaseController;
import pers.hsc.evats.core.model.AjaxJson;
import pers.hsc.evats.core.security.shiro.authz.annotation.RequiresPathPermission;
import pers.hsc.evats.core.utils.email.EmailResult;
import pers.hsc.evats.modules.email.service.IEmailSendService;

/**
 * 邮件发送
 * 
 * @author hsc
 *
 * Mar 29, 2018
 */
@Controller
@RequestMapping("${admin.url.prefix}/email/send")
@RequiresPathPermission("email:send")
public class EmailSendController extends BaseController {
	@Autowired
	private IEmailSendService emailSendService;

	@RequestMapping(value = "/email", method = RequestMethod.GET)
	public String email(HttpServletRequest request, HttpServletResponse response) {
		return display("email");
	}

	@RequestMapping(value = "/sendEmailByContent", method = RequestMethod.POST)
	@ResponseBody
	public AjaxJson sendEmailByContent(HttpServletRequest request, HttpServletResponse response) {
		AjaxJson ajaxJson = new AjaxJson();
		ajaxJson.success("邮件发送成功");
		try {
			String email = request.getParameter("email");
			String subject = request.getParameter("subject");
			String content = request.getParameter("content");
			content = StringEscapeUtils.unescapeHtml4(content);
			EmailResult emailResult = emailSendService.sendSyncEmail(email, subject, content);
			if (!emailResult.isSuccess()) {
				ajaxJson.fail(emailResult.getMsg());
			}
		} catch (Exception e) {
			e.printStackTrace();
			ajaxJson.fail("邮件发送失败");
		}
		return ajaxJson;
	}

	@RequestMapping(value = "/sendEmailByCode", method = RequestMethod.POST)
	@ResponseBody
	public AjaxJson sendEmailByCode(HttpServletRequest request, HttpServletResponse response) {
		AjaxJson ajaxJson = new AjaxJson();
		ajaxJson.success("邮件发送成功");
		try {
			String email = request.getParameter("email");
			String code = request.getParameter("code");
			String data = request.getParameter("data");
			EmailResult emailResult = null;
			if (!StringUtils.isEmpty(data)) {
				String[] datas = data.split(",");
				emailResult = emailSendService.sendSyncEmailByCode(email, code, datas);
			} else {
				emailResult = emailSendService.sendSyncEmailByCode(email, code);
			}

			if (!emailResult.isSuccess()) {
				ajaxJson.fail(emailResult.getMsg());
			}
		} catch (Exception e) {
			e.printStackTrace();
			ajaxJson.fail("邮件发送失败");
		}
		return ajaxJson;
	}
}