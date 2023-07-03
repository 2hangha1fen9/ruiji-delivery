package cloud.zhfsmy.ruijidelivery.controller;

import cloud.zhfsmy.ruijidelivery.common.R;
import cloud.zhfsmy.ruijidelivery.entity.Employee;
import cloud.zhfsmy.ruijidelivery.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Resource
    private EmployeeService employeeService;

    /**
     * 用户登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //获取密码MD5
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //查询用户信息
        LambdaQueryWrapper<Employee> query = new LambdaQueryWrapper<>();
        query.eq(Employee::getUsername, employee.getUsername()).eq(Employee::getPassword, password);
        Employee emp = employeeService.getOne(query);
        //如果用户为空登录失败
        if (emp == null) {
            return R.error("用户名或密码错误!");
        }
        //如果用户被禁用禁止登录
        if (emp.getStatus() == 0) {
            return R.error("用户禁止登录!");
        }
        //登录成功保存用户session
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清除session数据
        request.getSession().removeAttribute("employee");
        return R.success("退出登录成功");
    }

    /**
     * 获取员工列表
     *
     * @param page
     * @return
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(int page, int pageSize, String name) {
        //模糊查询名称
        LambdaQueryWrapper<Employee> queryWarpper = new LambdaQueryWrapper<>();
        if (name != null) {
            queryWarpper.like(Employee::getName, name);
        }
        //获取结果
        Page<Employee> pageResult = employeeService.page(new Page<>(page, pageSize), queryWarpper);
        if (pageResult == null) {
            return R.error("数据获取失败");
        }
        return R.success(pageResult);
    }
}
