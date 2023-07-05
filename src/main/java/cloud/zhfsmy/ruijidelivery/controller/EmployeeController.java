package cloud.zhfsmy.ruijidelivery.controller;

import cloud.zhfsmy.ruijidelivery.common.BusinessException;
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
            throw new BusinessException("用户名或密码错误!");
        }
        //如果用户被禁用禁止登录
        if (emp.getStatus() == 0) {
            throw new BusinessException("用户禁止登录!");
        }
        //登录成功保存用户session
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 用户注销
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清除session数据
        request.getSession().removeAttribute("employee");
        return R.success("退出登录成功");
    }

    /**
     * 获取员工列表
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(int page, int pageSize, String name) {
        //模糊查询名称
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        if (name != null) {
            queryWrapper.like(Employee::getName, name);
        }
        //获取结果
        Page<Employee> pageResult = employeeService.page(new Page<>(page, pageSize), queryWrapper);
        if (pageResult == null) {
            throw new BusinessException("数据获取失败");
        }
        return R.success(pageResult);
    }

    /**
     * 根据员工ID获取员工信息
     */
    @GetMapping("/{id}")
    public R<Employee> getEmployeeById(@PathVariable Long id) {
        LambdaQueryWrapper<Employee> query = new LambdaQueryWrapper<>();
        query.eq(Employee::getId, id);
        Employee employee = employeeService.getOne(query);
        if (employee == null) {
            throw new BusinessException("数据获取失败");
        }
        return R.success(employee);
    }

    /**
     * 新增员工
     */
    @PostMapping
    public R<String> addEmployee(@RequestBody Employee employee) {
        checkUser(employee);

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setStatus(1);
        boolean save = employeeService.save(employee);
        if (!save) {
            throw new BusinessException("员工添加失败");
        }
        return R.success("员工添加成功");
    }

    /**
     * 修改员工
     */
    @PutMapping
    public R<String> editEmployee(@RequestBody Employee employee) {
        LambdaQueryWrapper<Employee> query = new LambdaQueryWrapper<>();
        query.eq(Employee::getId, employee.getId());
        Employee exEmployee = employeeService.getOne(query);
        if (exEmployee == null) {
            throw new BusinessException("员工不存在");
        }
        boolean save = employeeService.updateById(employee);
        if (!save) {
            throw new BusinessException("员工修改失败");
        }
        return R.success("员工修改失败");
    }

    /**
     * 模型验证
     */
    private void checkUser(Employee employee) {
        if (employee.getUsername() == null || employee.getUsername().length() == 0) {
            throw new BusinessException("用户名不能为空");
        }
        if (employee.getName() == null || employee.getName().length() == 0) {
            throw new BusinessException("姓名不能为空");
        }
        if (employee.getPhone() == null || employee.getPhone().length() == 0) {
            throw new BusinessException("手机号不能为空");
        }
        if (employee.getIdNumber() == null || employee.getIdNumber().length() == 0) {
            throw new BusinessException("身份证号不能为空");
        }
        //修改时不验证
        if (employee.getId() == null) {
            LambdaQueryWrapper<Employee> exQuery = new LambdaQueryWrapper<>();
            exQuery.eq(Employee::getUsername, employee.getUsername()).or()
                    .eq(Employee::getPhone, employee.getPhone()).or()
                    .eq(Employee::getIdNumber, employee.getIdNumber());
            Employee exEmployee = employeeService.getOne(exQuery);
            if (exEmployee != null) {
                throw new BusinessException("员工已存在");
            }
        }
    }
}
