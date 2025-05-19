<?php
namespace App\Controller;

use App\Repository\CollaborationRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Process\Exception\ProcessFailedException;
use Symfony\Component\Process\Process;
use Symfony\Component\Routing\Annotation\Route;

class BudgetController extends AbstractController
{
    #[Route('/predict-budget', name: 'predict_budget')]
    public function predictBudget(CollaborationRepository $collaborationRepository): Response
    {
        // Get all collaborations
        $collaborations = $collaborationRepository->findAll();

        // Collect collaboration IDs to send to Python
        $ids = array_map(fn($collaboration) => $collaboration->getId(), $collaborations);

        // Convert IDs to JSON string to send as argument
        $idsJson = json_encode($ids);

        // Path to the Python script
        $pythonScriptPath = $this->getParameter('kernel.project_dir') . '/public/scripts/budget_model.py';

        // Execute the Python script with JSON data as argument
        $process = new Process(['python', $pythonScriptPath, $idsJson]);
        $process->run();

        // Check for errors in script execution
        if (!$process->isSuccessful()) {
            throw new ProcessFailedException($process);
        }

        // Decode JSON output from Python
        $jsonResult = trim($process->getOutput());
        $deviations = json_decode($jsonResult, true);

        if (json_last_error() !== JSON_ERROR_NONE) {
            throw new \Exception("Invalid JSON output from Python script");
        }

        // Render the result in Twig
        return $this->render('budget/prediction_result.html.twig', [
            'deviations' => $deviations,
            'collaborations' => $collaborations,
        ]);
    }
}
